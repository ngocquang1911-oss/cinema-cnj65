package com.cnj65.cinema.dao;

import com.cnj65.cinema.util.Constants;

import com.cnj65.cinema.config.DBContext;
import com.cnj65.cinema.model.Ticket;
import com.cnj65.cinema.model.TicketDetail;
import com.cnj65.cinema.util.TicketCodeUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TicketDAO {

    private final ShowtimeSeatDAO showtimeSeatDAO = new ShowtimeSeatDAO();

    /**
     * TAO VE - LUONG NGHIEP VU QUAN TRONG NHAT CUA HE THONG (Logic 5 trong ban goc).
     * Thuc hien trong 1 TRANSACTION duy nhat:
     *   1) Xac nhan tung ghe dang LOCKED boi user nay -> chuyen thanh BOOKED
     *   2) Neu BAT KY ghe nao khong con hop le (het han giu ghe, da bi nguoi
     *      khac dat) -> ROLLBACK toan bo, khong tao ve, tra ve null
     *   3) INSERT tickets, INSERT ticket_details (tung ghe), INSERT payments
     *   4) COMMIT
     *
     * seatPrices: map seatId -> gia tien (da tinh theo loai ghe VIP/NORMAL/COUPLE)
     *
     * @return ma ve neu thanh cong, null neu that bai (ghe khong con hop le)
     */
    public String createTicket(Integer userId, Integer staffId, String guestName, String guestPhone,
                                int showtimeId, Map<Integer, BigDecimal> seatPrices, String paymentMethod) {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Buoc 1: xac nhan tung ghe - neu 1 ghe that bai thi huy toan bo giao dich
            for (Integer seatId : seatPrices.keySet()) {
                boolean ok;
                if (userId != null) {
                    ok = showtimeSeatDAO.confirmBooked(conn, showtimeId, seatId, userId);
                } else {
                    // Nhan vien ban tai quay - dat truc tiep khong can buoc giu ghe truoc
                    ok = showtimeSeatDAO.directBook(conn, showtimeId, seatId);
                }
                if (!ok) {
                    conn.rollback();
                    return null; // Ghe khong con hop le -> bao FE "vui long chon lai"
                }
            }

            // Buoc 2: tinh tong tien
            BigDecimal total = BigDecimal.ZERO;
            for (BigDecimal p : seatPrices.values()) total = total.add(p);

            // Buoc 3: tao ticket
            String ticketCode = TicketCodeUtil.generate();
            String sqlTicket = "INSERT INTO tickets (ticket_code, user_id, showtime_id, staff_id, " +
                    "guest_name, guest_phone, total_amount, status) VALUES (?,?,?,?,?,?,?, '" + Constants.TICKET_PAID + "')";
            int ticketId;
            try (PreparedStatement ps = conn.prepareStatement(sqlTicket, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, ticketCode);
                if (userId != null) ps.setInt(2, userId); else ps.setNull(2, Types.INTEGER);
                ps.setInt(3, showtimeId);
                if (staffId != null) ps.setInt(4, staffId); else ps.setNull(4, Types.INTEGER);
                ps.setString(5, guestName);
                ps.setString(6, guestPhone);
                ps.setBigDecimal(7, total);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    ticketId = keys.getInt(1);
                }
            }

            // Buoc 4: tao ticket_details (tung ghe)
            String sqlDetail = "INSERT INTO ticket_details (ticket_id, seat_id, price) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlDetail)) {
                for (Map.Entry<Integer, BigDecimal> e : seatPrices.entrySet()) {
                    ps.setInt(1, ticketId);
                    ps.setInt(2, e.getKey());
                    ps.setBigDecimal(3, e.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // Buoc 5: tao payment
            String sqlPayment = "INSERT INTO payments (ticket_id, amount, method, status) VALUES (?,?,?,'SUCCESS')";
            try (PreparedStatement ps = conn.prepareStatement(sqlPayment)) {
                ps.setInt(1, ticketId);
                ps.setBigDecimal(2, total);
                ps.setString(3, paymentMethod);
                ps.executeUpdate();
            }

            conn.commit();
            return ticketCode;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Loi tao ve (da rollback): " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    private static final String BASE_SELECT =
        "SELECT t.*, u.full_name AS customer_name, m.title AS movie_title, r.name AS room_name, " +
        "st.show_date, st.start_time " +
        "FROM tickets t " +
        "LEFT JOIN users u ON t.user_id = u.id " +
        "JOIN showtimes st ON t.showtime_id = st.id " +
        "JOIN movies m ON st.movie_id = m.id " +
        "JOIN rooms r ON st.room_id = r.id ";

    public Ticket findByCode(String ticketCode) {
        String sql = BASE_SELECT + "WHERE t.ticket_code = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ticket t = mapRow(rs);
                    t.setDetails(findDetails(t.getId()));
                    return t;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tim ve theo ma: " + e.getMessage(), e);
        }
        return null;
    }

    public Ticket findById(int id) {
        String sql = BASE_SELECT + "WHERE t.id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ticket t = mapRow(rs);
                    t.setDetails(findDetails(t.getId()));
                    return t;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tim ve theo id: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Ticket> findByUser(int userId) {
        List<Ticket> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE t.user_id = ? ORDER BY t.created_at DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay ve theo user: " + e.getMessage(), e);
        }
        return list;
    }

    /** Danh sach ve cho Admin, co the loc theo trang thai */
    public List<Ticket> findAll(String status) {
        List<Ticket> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1 ");
        if (status != null && !status.isEmpty()) sql.append("AND t.status = ? ");
        sql.append("ORDER BY t.created_at DESC LIMIT 500");
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (status != null && !status.isEmpty()) ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach ve: " + e.getMessage(), e);
        }
        return list;
    }

    private List<TicketDetail> findDetails(int ticketId) {
        List<TicketDetail> list = new ArrayList<>();
        String sql = "SELECT td.*, s.seat_code FROM ticket_details td " +
                     "JOIN seats s ON td.seat_id = s.id WHERE td.ticket_id = ? ORDER BY s.seat_code";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TicketDetail td = new TicketDetail();
                    td.setId(rs.getInt("id"));
                    td.setTicketId(rs.getInt("ticket_id"));
                    td.setSeatId(rs.getInt("seat_id"));
                    td.setSeatCode(rs.getString("seat_code"));
                    td.setPrice(rs.getBigDecimal("price"));
                    list.add(td);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay chi tiet ve: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * HUY VE (khach tu huy, hoac Admin huy).
     * Trong 1 transaction: doi trang thai ve -> CANCELLED, tra tung ghe ve AVAILABLE.
     * Chi cho phep huy khi ve dang PAID va suat chieu chua dien ra.
     */
    public boolean cancelTicket(int ticketId) {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            Ticket t = findById(ticketId);
            if (t == null || !"PAID".equals(t.getStatus())) {
                conn.rollback();
                return false;
            }

            String sqlUpdate = "UPDATE tickets SET status='CANCELLED' WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setInt(1, ticketId);
                ps.executeUpdate();
            }

            for (TicketDetail td : t.getDetails()) {
                showtimeSeatDAO.releaseBookedSeat(conn, t.getShowtimeId(), td.getSeatId());
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ignored) {} }
            throw new RuntimeException("Loi huy ve: " + e.getMessage(), e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {} }
        }
    }

    /** Nhan vien check-in ve tai rap: PAID -> CHECKED_IN */
    public boolean checkin(String ticketCode) {
        String sql = "UPDATE tickets SET status='CHECKED_IN', checkin_at=NOW() " +
                     "WHERE ticket_code=? AND status='PAID'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketCode);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Loi check-in: " + e.getMessage(), e);
        }
    }

    // ===================== BAO CAO THONG KE (Admin Dashboard) =====================

    public BigDecimal totalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount),0) FROM tickets WHERE status IN ('PAID','CHECKED_IN')";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            throw new RuntimeException("Loi tinh doanh thu: " + e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }

    public int totalTicketsSold() {
        String sql = "SELECT COUNT(*) FROM tickets WHERE status IN ('PAID','CHECKED_IN')";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Loi dem ve: " + e.getMessage(), e);
        }
        return 0;
    }

    /** Doanh thu theo tung ngay trong N ngay gan nhat - dung ve bieu do */
    public List<Object[]> revenueByDay(int days) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT DATE(created_at) AS d, COALESCE(SUM(total_amount),0) " +
                     "FROM tickets WHERE status IN ('PAID','CHECKED_IN') " +
                     "AND created_at >= (CURDATE() - INTERVAL ? DAY) " +
                     "GROUP BY DATE(created_at) ORDER BY d";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Object[]{ rs.getDate(1), rs.getBigDecimal(2) });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi doanh thu theo ngay: " + e.getMessage(), e);
        }
        return list;
    }

    /** Top phim ban chay nhat theo so ve */
    public List<Object[]> topMoviesBySales(int limit) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT m.title, COUNT(td.id) AS sold " +
                     "FROM ticket_details td " +
                     "JOIN tickets t ON td.ticket_id = t.id " +
                     "JOIN showtimes st ON t.showtime_id = st.id " +
                     "JOIN movies m ON st.movie_id = m.id " +
                     "WHERE t.status IN ('PAID','CHECKED_IN') " +
                     "GROUP BY m.id, m.title ORDER BY sold DESC LIMIT ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Object[]{ rs.getString(1), rs.getInt(2) });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi top phim: " + e.getMessage(), e);
        }
        return list;
    }

    private Ticket mapRow(ResultSet rs) throws SQLException {
        Ticket t = new Ticket();
        t.setId(rs.getInt("id"));
        t.setTicketCode(rs.getString("ticket_code"));
        int userId = rs.getInt("user_id");
        t.setUserId(rs.wasNull() ? null : userId);
        t.setCustomerName(rs.getString("customer_name"));
        t.setShowtimeId(rs.getInt("showtime_id"));
        t.setMovieTitle(rs.getString("movie_title"));
        t.setRoomName(rs.getString("room_name"));
        t.setShowDate(rs.getDate("show_date").toLocalDate());
        t.setStartTime(rs.getTime("start_time").toLocalTime());
        int staffId = rs.getInt("staff_id");
        t.setStaffId(rs.wasNull() ? null : staffId);
        t.setGuestName(rs.getString("guest_name"));
        t.setGuestPhone(rs.getString("guest_phone"));
        t.setTotalAmount(rs.getBigDecimal("total_amount"));
        t.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) t.setCreatedAt(ts.toLocalDateTime());
        Timestamp ci = rs.getTimestamp("checkin_at");
        if (ci != null) t.setCheckinAt(ci.toLocalDateTime());
        return t;
    }
}
