package com.cnj65.cinema.dao;

import com.cnj65.cinema.util.Constants;

import com.cnj65.cinema.config.DBContext;
import com.cnj65.cinema.model.ShowtimeSeat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO QUAN TRONG NHAT cua he thong.
 * Xu ly toan bo logic "chong trung ghe" (Logic 2 trong ban nghiep vu goc)
 * va "giu ghe tam thoi" khi khach dang chon ghe nhung chua thanh toan.
 *
 * Nguyen tac: moi thao tac doi trang thai ghe deu dung UPDATE ... WHERE status='...'
 * va kiem tra so dong bi anh huong (affected rows). Neu = 0 nghia la ghe da
 * bi nguoi khac giu/mua truoc do -> tranh duoc race condition khi 2 nguoi
 * bam dat cung 1 ghe cung luc, MA KHONG CAN khoa (lock) o tang ung dung.
 */
public class ShowtimeSeatDAO {

    private static final String BASE_SELECT =
        "SELECT ss.*, s.seat_code, s.seat_type FROM showtime_seats ss " +
        "JOIN seats s ON ss.seat_id = s.id ";

    /** Lay so do ghe day du cua 1 suat chieu (dung ve JSP render luoi ghe) */
    public List<ShowtimeSeat> findByShowtime(int showtimeId) {
        List<ShowtimeSeat> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE ss.showtime_id = ? ORDER BY s.seat_row, s.seat_number";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay so do ghe: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * GIU GHE TAM THOI (khi khach bam chon 1 ghe tren so do).
     * Chi thanh cong (tra ve true) neu ghe dang o trang thai AVAILABLE.
     * Day chinh la co che tranh 2 nguoi chon trung 1 ghe cung luc.
     */
    public boolean lockSeat(int showtimeId, int seatId, int userId) {
        String sql = "UPDATE showtime_seats SET status='" + Constants.SEAT_LOCKED + "', locked_by=?, locked_at=NOW() " +
                     "WHERE showtime_id=? AND seat_id=? AND status='" + Constants.SEAT_AVAILABLE + "'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, showtimeId);
            ps.setInt(3, seatId);
            int affected = ps.executeUpdate();
            return affected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Loi giu ghe: " + e.getMessage(), e);
        }
    }

    /** NHA GHE (khach bo chon ghe, hoac huy don truoc khi thanh toan) */
    public boolean unlockSeat(int showtimeId, int seatId, int userId) {
        String sql = "UPDATE showtime_seats SET status='" + Constants.SEAT_AVAILABLE + "', locked_by=NULL, locked_at=NULL " +
                     "WHERE showtime_id=? AND seat_id=? AND status='" + Constants.SEAT_LOCKED + "' AND locked_by=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            ps.setInt(2, seatId);
            ps.setInt(3, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Loi nha ghe: " + e.getMessage(), e);
        }
    }

    /**
     * XAC NHAN GHE DA BAN (dung trong transaction dat ve, sau khi thanh toan thanh cong).
     * Chi cho phep chuyen tu LOCKED (boi chinh user nay) -> BOOKED.
     * Neu ghe khong con o trang thai LOCKED cua user nay (vi du het han giu ghe
     * va bi he thong nha ra) thi se that bai -> Servlet se bao loi "ghe khong
     * con hieu luc, vui long chon lai".
     */
    public boolean confirmBooked(Connection conn, int showtimeId, int seatId, int userId) throws SQLException {
        String sql = "UPDATE showtime_seats SET status='" + Constants.SEAT_BOOKED + "', locked_by=NULL, locked_at=NULL " +
                     "WHERE showtime_id=? AND seat_id=? AND status='" + Constants.SEAT_LOCKED + "' AND locked_by=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            ps.setInt(2, seatId);
            ps.setInt(3, userId);
            return ps.executeUpdate() == 1;
        }
    }

    /** Danh cho Nhan vien ban ve tai quay: dat truc tiep AVAILABLE -> BOOKED (khong can giu ghe truoc) */
    public boolean directBook(Connection conn, int showtimeId, int seatId) throws SQLException {
        String sql = "UPDATE showtime_seats SET status='" + Constants.SEAT_BOOKED + "' " +
                     "WHERE showtime_id=? AND seat_id=? AND status='" + Constants.SEAT_AVAILABLE + "'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            ps.setInt(2, seatId);
            return ps.executeUpdate() == 1;
        }
    }

    /** Tra ghe ve AVAILABLE khi Huy ve (sau khi da BOOKED) */
    public void releaseBookedSeat(Connection conn, int showtimeId, int seatId) throws SQLException {
        String sql = "UPDATE showtime_seats SET status='" + Constants.SEAT_AVAILABLE + "' WHERE showtime_id=? AND seat_id=? AND status='" + Constants.SEAT_BOOKED + "'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, showtimeId);
            ps.setInt(2, seatId);
            ps.executeUpdate();
        }
    }

    /**
     * SCHEDULED JOB: tu dong nha cac ghe bi giu (LOCKED) qua lau ma khach
     * khong hoan tat thanh toan (qua thoi gian timeoutMinutes).
     * Duoc goi dinh ky boi SeatLockCleaner (xem package listener/util).
     */
    public int releaseExpiredLocks(int timeoutMinutes) {
        String sql = "UPDATE showtime_seats SET status='" + Constants.SEAT_AVAILABLE + "', locked_by=NULL, locked_at=NULL " +
                     "WHERE status='" + Constants.SEAT_LOCKED + "' AND locked_at < (NOW() - INTERVAL ? MINUTE)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, timeoutMinutes);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi nha ghe het han: " + e.getMessage(), e);
        }
    }

    private ShowtimeSeat mapRow(ResultSet rs) throws SQLException {
        ShowtimeSeat ss = new ShowtimeSeat();
        ss.setId(rs.getInt("id"));
        ss.setShowtimeId(rs.getInt("showtime_id"));
        ss.setSeatId(rs.getInt("seat_id"));
        ss.setSeatCode(rs.getString("seat_code"));
        ss.setSeatType(rs.getString("seat_type"));
        ss.setStatus(rs.getString("status"));
        int lockedBy = rs.getInt("locked_by");
        ss.setLockedBy(rs.wasNull() ? null : lockedBy);
        Timestamp ts = rs.getTimestamp("locked_at");
        if (ts != null) ss.setLockedAt(ts.toLocalDateTime());
        return ss;
    }
}
