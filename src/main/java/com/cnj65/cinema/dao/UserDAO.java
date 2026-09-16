package com.cnj65.cinema.dao;

import com.cnj65.cinema.config.DBContext;
import com.cnj65.cinema.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /** Tim user theo username - dung khi Login */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi truy van findByUsername: " + e.getMessage(), e);
        }
        return null;
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi truy van findById: " + e.getMessage(), e);
        }
        return null;
    }

    /** Kiem tra trung username/email khi dang ky */
    public boolean existsByUsernameOrEmail(String username, String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra trung: " + e.getMessage(), e);
        }
        return false;
    }

    /** Dang ky tai khoan moi - mac dinh role CUSTOMER */
    public int register(User u) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'CUSTOMER', 'ACTIVE')";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPhone());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi dang ky: " + e.getMessage(), e);
        }
        return -1;
    }

    /** Admin tao tai khoan Staff */
    public int createStaff(User u) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'STAFF', 'ACTIVE')";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPhone());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tao staff: " + e.getMessage(), e);
        }
        return -1;
    }

    public void updateProfile(int id, String fullName, String email, String phone) {
        String sql = "UPDATE users SET full_name=?, email=?, phone=? WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat ho so: " + e.getMessage(), e);
        }
    }

    public void changePassword(int id, String newHashedPassword) {
        String sql = "UPDATE users SET password=? WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi doi mat khau: " + e.getMessage(), e);
        }
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE users SET status=? WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat trang thai: " + e.getMessage(), e);
        }
    }

    /** Danh sach khach hang (role=CUSTOMER), co tim kiem theo ten/email/sdt */
    public List<User> searchCustomers(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='CUSTOMER' " +
                     "AND (full_name LIKE ? OR email LIKE ? OR phone LIKE ?) ORDER BY id DESC";
        String kw = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tim khach hang: " + e.getMessage(), e);
        }
        return list;
    }

    /** Tim khach hang theo SDT - dung cho Nhan vien ban ve tai quay */
    public User findCustomerByPhone(String phone) {
        String sql = "SELECT * FROM users WHERE phone = ? AND role='CUSTOMER'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tim khach theo SDT: " + e.getMessage(), e);
        }
        return null;
    }

    public List<User> findAllStaff() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='STAFF' ORDER BY id DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay danh sach staff: " + e.getMessage(), e);
        }
        return list;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }
}
