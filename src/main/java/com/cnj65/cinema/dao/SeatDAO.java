package com.cnj65.cinema.dao;

import com.cnj65.cinema.config.DBContext;
import com.cnj65.cinema.model.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    public List<Seat> findByRoom(int roomId) {
        List<Seat> list = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE room_id=? ORDER BY seat_row, seat_number";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach ghe: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Sinh ghe tu dong cho 1 phong theo so hang/cot.
     * coupleRowIndex: chi so hang (0-based) la ghe doi, -1 neu khong co ghe doi.
     * Dong thoi tao showtime_seats AVAILABLE cho tat ca suat chieu (neu co) cua phong nay.
     */
    public int generateSeatsForRoom(int roomId, int rows, int cols, int coupleRowIndex) {
        String insertSeat = "INSERT INTO seats (room_id, seat_row, seat_number, seat_code, seat_type) VALUES (?,?,?,?,?)";
        String deleteOld = "DELETE FROM seats WHERE room_id = ?";
        int total = 0;
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement del = conn.prepareStatement(deleteOld)) {
                del.setInt(1, roomId);
                del.executeUpdate();
            }
            try (PreparedStatement ins = conn.prepareStatement(insertSeat)) {
                for (int r = 0; r < rows; r++) {
                    char rowChar = (char) ('A' + r);
                    String seatType = (r == coupleRowIndex) ? "COUPLE" : (r >= rows - 2 ? "VIP" : "NORMAL");
                    for (int c = 1; c <= cols; c++) {
                        String code = String.format("%s%02d", rowChar, c);
                        ins.setInt(1, roomId);
                        ins.setString(2, String.valueOf(rowChar));
                        ins.setInt(3, c);
                        ins.setString(4, code);
                        ins.setString(5, seatType);
                        ins.addBatch();
                        total++;
                    }
                }
                ins.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Loi sinh ghe tu dong: " + e.getMessage(), e);
        }
        return total;
    }

    public boolean hasTickets(int seatId) {
        String sql = "SELECT COUNT(*) FROM ticket_details WHERE seat_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra ve theo ghe: " + e.getMessage(), e);
        }
        return false;
    }

    public void updateSeatType(int seatId, String seatType) {
        String sql = "UPDATE seats SET seat_type=? WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, seatType);
            ps.setInt(2, seatId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat loai ghe: " + e.getMessage(), e);
        }
    }

    private Seat mapRow(ResultSet rs) throws SQLException {
        Seat s = new Seat();
        s.setId(rs.getInt("id"));
        s.setRoomId(rs.getInt("room_id"));
        s.setSeatRow(rs.getString("seat_row"));
        s.setSeatNumber(rs.getInt("seat_number"));
        s.setSeatCode(rs.getString("seat_code"));
        s.setSeatType(rs.getString("seat_type"));
        return s;
    }
}
