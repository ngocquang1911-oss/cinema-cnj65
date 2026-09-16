package com.cnj65.cinema.controller.customer;

import com.cnj65.cinema.dao.GenreDAO;
import com.cnj65.cinema.dao.MovieDAO;
import com.cnj65.cinema.model.Genre;
import com.cnj65.cinema.model.Movie;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Danh sach phim + tim kiem + loc theo the loai/trang thai (nghiep vu 6,7,8 cua Customer) */
@WebServlet("/movies")
public class MovieListServlet extends HttpServlet {

    private final MovieDAO movieDAO = new MovieDAO();
    private final GenreDAO genreDAO = new GenreDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String genreIdParam = req.getParameter("genreId");
        String status = req.getParameter("status");

        Integer genreId = null;
        if (genreIdParam != null && !genreIdParam.isEmpty()) {
            try { genreId = Integer.parseInt(genreIdParam); } catch (NumberFormatException ignored) {}
        }

        List<Movie> movies = movieDAO.search(keyword, genreId, status);
        List<Genre> genres = genreDAO.findAll();

        req.setAttribute("movies", movies);
        req.setAttribute("genres", genres);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedGenreId", genreId);
        req.setAttribute("selectedStatus", status);
        req.getRequestDispatcher("/WEB-INF/views/customer/movie-list.jsp").forward(req, resp);
    }
}
