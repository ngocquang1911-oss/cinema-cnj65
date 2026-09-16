package com.cnj65.cinema.dao;

import com.cnj65.cinema.config.DBContext;
import com.cnj65.cinema.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> findAll() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY id";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach phong: " + e.getMessage(), e);
        }
        return list;
    }

    public Room findById(int id) {
        String sql = "SELECT * FROM rooms WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay phong: " + e.getMessage(), e);
        }
        return null;
    }

    public int insert(Room r) {
        String sql = "INSERT INTO rooms (name, room_type, total_seats, status) VALUES (?,?,?,?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getName());
            ps.setString(2, r.getRoomType());
            ps.setInt(3, r.getTotalSeats());
            ps.setString(4, r.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi them phong: " + e.getMessage(), e);
        }
        return -1;
    }

    public void update(Room r) {
        String sql = "UPDATE rooms SET name=?, room_type=?, status=? WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getName());
            ps.setString(2, r.getRoomType());
            ps.setString(3, r.getStatus());
            ps.setInt(4, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat phong: " + e.getMessage(), e);
        }
    }

    public void updateTotalSeats(int roomId, int totalSeats) {
        String sql = "UPDATE rooms SET total_seats=? WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, totalSeats);
            ps.setInt(2, roomId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat so ghe: " + e.getMessage(), e);
        }
    }

    public boolean hasShowtimes(int roomId) {
        String sql = "SELECT COUNT(*) FROM showtimes WHERE room_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra suat chieu: " + e.getMessage(), e);
        }
        return false;
    }

    public void delete(int id) {
        String sql = "DELETE FROM rooms WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi xoa phong: " + e.getMessage(), e);
        }
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setId(rs.getInt("id"));
        r.setName(rs.getString("name"));
        r.setRoomType(rs.getString("room_type"));
        r.setTotalSeats(rs.getInt("total_seats"));
        r.setStatus(rs.getString("status"));
        return r;
    }
}
