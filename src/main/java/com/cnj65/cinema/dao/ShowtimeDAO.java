package com.cnj65.cinema.dao;

import com.cnj65.cinema.config.DBContext;
import com.cnj65.cinema.model.Showtime;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeDAO {

    private static final String BASE_SELECT =
        "SELECT st.*, m.title AS movie_title, r.name AS room_name " +
        "FROM showtimes st " +
        "JOIN movies m ON st.movie_id = m.id " +
        "JOIN rooms r ON st.room_id = r.id ";

    /** Danh sach suat chieu cua 1 phim theo ngay - dung cho trang khach chon suat */
    public List<Showtime> findByMovieAndDate(int movieId, LocalDate date) {
        List<Showtime> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE st.movie_id=? AND st.show_date=? AND st.status='ACTIVE' " +
                     "ORDER BY st.start_time";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay suat chieu: " + e.getMessage(), e);
        }
        return list;
    }

    public Showtime findById(int id) {
        String sql = BASE_SELECT + "WHERE st.id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay suat chieu theo id: " + e.getMessage(), e);
        }
        return null;
    }

    /** Danh sach toan bo suat chieu - dung cho Admin, co the loc theo ngay */
    public List<Showtime> findAll(LocalDate date) {
        List<Showtime> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1 ");
        if (date != null) sql.append("AND st.show_date = ? ");
        sql.append("ORDER BY st.show_date DESC, st.start_time DESC");

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (date != null) ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach suat chieu: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * LOGIC QUAN TRONG: kiem tra 1 phong co bi trung lich voi suat chieu khac
     * trong cung 1 ngay hay khong (khoang [start,end) giao nhau).
     * excludeShowtimeId dung khi Sua suat chieu (bo qua chinh no).
     */
    public boolean hasConflict(int roomId, LocalDate date, java.time.LocalTime start,
                                java.time.LocalTime end, Integer excludeShowtimeId) {
        String sql = "SELECT COUNT(*) FROM showtimes " +
                     "WHERE room_id = ? AND show_date = ? AND status='ACTIVE' " +
                     "AND (? < end_time AND ? > start_time) " +
                     (excludeShowtimeId != null ? "AND id <> ? " : "");
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(start));
            ps.setTime(4, Time.valueOf(end));
            if (excludeShowtimeId != null) ps.setInt(5, excludeShowtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra trung lich: " + e.getMessage(), e);
        }
        return false;
    }

    /** Them suat chieu MOI va tu dong sinh showtime_seats cho tat ca ghe cua phong (transaction) */
    public int insert(Showtime st) {
        String sqlInsert = "INSERT INTO showtimes (movie_id, room_id, show_date, start_time, end_time, " +
                            "price_normal, price_vip, price_couple, status) VALUES (?,?,?,?,?,?,?,?,'ACTIVE')";
        String sqlGenSeats = "INSERT INTO showtime_seats (showtime_id, seat_id, status) " +
                              "SELECT ?, id, 'AVAILABLE' FROM seats WHERE room_id = ?";
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            int newId;
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                bindShowtime(ps, st);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    newId = keys.getInt(1);
                }
            }
            try (PreparedStatement ps2 = conn.prepareStatement(sqlGenSeats)) {
                ps2.setInt(1, newId);
                ps2.setInt(2, st.getRoomId());
                ps2.executeUpdate();
            }
            conn.commit();
            return newId;
        } catch (SQLException e) {
            throw new RuntimeException("Loi them suat chieu: " + e.getMessage(), e);
        }
    }

    public void update(Showtime st) {
        String sql = "UPDATE showtimes SET movie_id=?, room_id=?, show_date=?, start_time=?, end_time=?, " +
                     "price_normal=?, price_vip=?, price_couple=? WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindShowtime(ps, st);
            ps.setInt(9, st.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat suat chieu: " + e.getMessage(), e);
        }
    }

    public boolean hasSoldTickets(int showtimeId) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE showtime_id=? AND status IN ('PAID','CHECKED_IN')";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra ve da ban: " + e.getMessage(), e);
        }
        return false;
    }

    public void cancel(int id) {
        String sql = "UPDATE showtimes SET status='CANCELLED' WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi huy suat chieu: " + e.getMessage(), e);
        }
    }

    private void bindShowtime(PreparedStatement ps, Showtime st) throws SQLException {
        ps.setInt(1, st.getMovieId());
        ps.setInt(2, st.getRoomId());
        ps.setDate(3, Date.valueOf(st.getShowDate()));
        ps.setTime(4, Time.valueOf(st.getStartTime()));
        ps.setTime(5, Time.valueOf(st.getEndTime()));
        ps.setBigDecimal(6, st.getPriceNormal());
        ps.setBigDecimal(7, st.getPriceVip());
        ps.setBigDecimal(8, st.getPriceCouple());
    }

    private Showtime mapRow(ResultSet rs) throws SQLException {
        Showtime st = new Showtime();
        st.setId(rs.getInt("id"));
        st.setMovieId(rs.getInt("movie_id"));
        st.setMovieTitle(rs.getString("movie_title"));
        st.setRoomId(rs.getInt("room_id"));
        st.setRoomName(rs.getString("room_name"));
        st.setShowDate(rs.getDate("show_date").toLocalDate());
        st.setStartTime(rs.getTime("start_time").toLocalTime());
        st.setEndTime(rs.getTime("end_time").toLocalTime());
        st.setPriceNormal(rs.getBigDecimal("price_normal"));
        st.setPriceVip(rs.getBigDecimal("price_vip"));
        st.setPriceCouple(rs.getBigDecimal("price_couple"));
        st.setStatus(rs.getString("status"));
        return st;
    }
}
