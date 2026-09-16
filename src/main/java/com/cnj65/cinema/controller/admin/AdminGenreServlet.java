package com.cnj65.cinema.controller.admin;

import com.cnj65.cinema.dao.GenreDAO;
import com.cnj65.cinema.model.Genre;
import com.cnj65.cinema.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Quan ly The loai phim (CRUD don gian, 1 servlet xu ly ca list + add + edit + delete
 * qua tham so "action" de giam so luong file — phu hop voi 1 resource nho, khong
 * co nhieu nghiep vu phuc tap nhu Movie/Showtime).
 */
@WebServlet("/admin/genres")
public class AdminGenreServlet extends HttpServlet {

    private final GenreDAO genreDAO = new GenreDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("genres", genreDAO.findAll());
        req.getRequestDispatcher("/WEB-INF/views/admin/genre-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("add".equals(action)) {
            String name = req.getParameter("name");
            if (!ValidationUtil.isBlank(name)) genreDAO.insert(name.trim());
        } else if ("edit".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            String name = req.getParameter("name");
            if (!ValidationUtil.isBlank(name)) genreDAO.update(id, name.trim());
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            if (genreDAO.hasMovies(id)) {
                resp.sendRedirect(req.getContextPath() + "/admin/genres?error=in_use");
                return;
            }
            genreDAO.delete(id);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/genres");
    }
}
