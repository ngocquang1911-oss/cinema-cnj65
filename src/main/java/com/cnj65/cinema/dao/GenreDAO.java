package com.cnj65.cinema.dao;

import com.cnj65.cinema.config.DBContext;
import com.cnj65.cinema.model.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {

    public List<Genre> findAll() {
        List<Genre> list = new ArrayList<>();
        String sql = "SELECT * FROM genres ORDER BY name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Genre(rs.getInt("id"), rs.getString("name")));
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach the loai: " + e.getMessage(), e);
        }
        return list;
    }

    public int insert(String name) {
        String sql = "INSERT INTO genres (name) VALUES (?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi them the loai: " + e.getMessage(), e);
        }
        return -1;
    }

    public void update(int id, String name) {
        String sql = "UPDATE genres SET name=? WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi sua the loai: " + e.getMessage(), e);
        }
    }

    /** Kiem tra con phim thuoc the loai nay khong truoc khi xoa */
    public boolean hasMovies(int genreId) {
        String sql = "SELECT COUNT(*) FROM movies WHERE genre_id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, genreId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra the loai: " + e.getMessage(), e);
        }
        return false;
    }

    public void delete(int id) {
        String sql = "DELETE FROM genres WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi xoa the loai: " + e.getMessage(), e);
        }
    }
}
