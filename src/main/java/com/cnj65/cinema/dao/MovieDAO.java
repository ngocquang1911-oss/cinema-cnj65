package com.cnj65.cinema.dao;

import com.cnj65.cinema.config.DBContext;
import com.cnj65.cinema.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    private static final String BASE_SELECT =
            "SELECT m.*, g.name AS genre_name FROM movies m JOIN genres g ON m.genre_id = g.id ";

    /** Danh sach phim theo trang thai (NOW_SHOWING / COMING_SOON), dung cho trang khach */
    public List<Movie> findByStatus(String status) {
        List<Movie> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE m.status = ? ORDER BY m.release_date DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay phim theo trang thai: " + e.getMessage(), e);
        }
        return list;
    }

    /** Tim kiem + loc phim (dung cho Admin va trang tim kiem khach hang) */
    public List<Movie> search(String keyword, Integer genreId, String status) {
        List<Movie> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND m.title LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (genreId != null) {
            sql.append("AND m.genre_id = ? ");
            params.add(genreId);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND m.status = ? ");
            params.add(status);
        }
        sql.append("ORDER BY m.id DESC");

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tim kiem phim: " + e.getMessage(), e);
        }
        return list;
    }

    public Movie findById(int id) {
        String sql = BASE_SELECT + "WHERE m.id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi lay chi tiet phim: " + e.getMessage(), e);
        }
        return null;
    }

    public int insert(Movie m) {
        String sql = "INSERT INTO movies (genre_id, title, director, actors, duration, description, " +
                     "poster_url, trailer_url, release_date, age_rating, status) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindMovie(ps, m);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi them phim: " + e.getMessage(), e);
        }
        return -1;
    }

    public void update(Movie m) {
        String sql = "UPDATE movies SET genre_id=?, title=?, director=?, actors=?, duration=?, " +
                     "description=?, poster_url=?, trailer_url=?, release_date=?, age_rating=?, status=? " +
                     "WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindMovie(ps, m);
            ps.setInt(12, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat phim: " + e.getMessage(), e);
        }
    }

    /** Kiem tra phim con suat chieu hay khong truoc khi cho xoa (rang buoc du lieu) */
    public boolean hasShowtimes(int movieId) {
        String sql = "SELECT COUNT(*) FROM showtimes WHERE movie_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi kiem tra suat chieu: " + e.getMessage(), e);
        }
        return false;
    }

    public void delete(int id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi xoa phim: " + e.getMessage(), e);
        }
    }

    /** An phim thay vi xoa cung du lieu (dung khi phim da co lich su ve) */
    public void updateStatus(int id, String status) {
        String sql = "UPDATE movies SET status=? WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat trang thai phim: " + e.getMessage(), e);
        }
    }

    private void bindMovie(PreparedStatement ps, Movie m) throws SQLException {
        ps.setInt(1, m.getGenreId());
        ps.setString(2, m.getTitle());
        ps.setString(3, m.getDirector());
        ps.setString(4, m.getActors());
        ps.setInt(5, m.getDuration());
        ps.setString(6, m.getDescription());
        ps.setString(7, m.getPosterUrl());
        ps.setString(8, m.getTrailerUrl());
        ps.setDate(9, m.getReleaseDate() != null ? Date.valueOf(m.getReleaseDate()) : null);
        ps.setString(10, m.getAgeRating());
        ps.setString(11, m.getStatus());
    }

    private Movie mapRow(ResultSet rs) throws SQLException {
        Movie m = new Movie();
        m.setId(rs.getInt("id"));
        m.setGenreId(rs.getInt("genre_id"));
        m.setGenreName(rs.getString("genre_name"));
        m.setTitle(rs.getString("title"));
        m.setDirector(rs.getString("director"));
        m.setActors(rs.getString("actors"));
        m.setDuration(rs.getInt("duration"));
        m.setDescription(rs.getString("description"));
        m.setPosterUrl(rs.getString("poster_url"));
        m.setTrailerUrl(rs.getString("trailer_url"));
        Date d = rs.getDate("release_date");
        if (d != null) m.setReleaseDate(d.toLocalDate());
        m.setAgeRating(rs.getString("age_rating"));
        m.setStatus(rs.getString("status"));
        return m;
    }
}
