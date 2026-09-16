package com.cnj65.cinema.controller.admin;

import com.cnj65.cinema.dao.GenreDAO;
import com.cnj65.cinema.dao.MovieDAO;
import com.cnj65.cinema.model.Movie;
import com.cnj65.cinema.util.Constants;
import com.cnj65.cinema.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Quan ly Phim (CRUD day du). Dung tham so "action" de dieu huong giua
 * man hinh Danh sach va man hinh Form (them/sua) trong CUNG 1 servlet,
 * giup giam so luong URL can nho khi lien ket tu JSP.
 *
 *   GET  /admin/movies                  -> danh sach (co the loc keyword/genreId/status)
 *   GET  /admin/movies?action=form      -> form them moi
 *   GET  /admin/movies?action=form&id=5 -> form sua phim id=5
 *   POST /admin/movies (action=save)    -> luu (insert neu khong co id, update neu co)
 *   POST /admin/movies (action=delete)  -> an phim (chuyen status=ENDED) neu da co suat chieu,
 *                                          xoa han neu chua co suat chieu nao
 */
@WebServlet("/admin/movies")
public class AdminMovieServlet extends HttpServlet {

    private final MovieDAO movieDAO = new MovieDAO();
    private final GenreDAO genreDAO = new GenreDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("form".equals(action)) {
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                Movie movie = movieDAO.findById(Integer.parseInt(idParam));
                if (movie == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/movies");
                    return;
                }
                req.setAttribute("movie", movie);
            }
            req.setAttribute("genres", genreDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/admin/movie-form.jsp").forward(req, resp);
            return;
        }

        // Man hinh danh sach (mac dinh)
        String keyword = req.getParameter("keyword");
        String statusParam = req.getParameter("status");
        req.setAttribute("movies", movieDAO.search(keyword, null, statusParam));
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedStatus", statusParam);
        req.getRequestDispatcher("/WEB-INF/views/admin/movie-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            if (movieDAO.hasShowtimes(id)) {
                // Da co lich su suat chieu/ve -> khong xoa cung du lieu, chi an di
                movieDAO.updateStatus(id, Constants.MOVIE_ENDED);
            } else {
                movieDAO.delete(id);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/movies");
            return;
        }

        // action=save (dung chung cho ca them moi va cap nhat)
        String idParam = req.getParameter("id");
        String title = req.getParameter("title");
        String director = req.getParameter("director");
        String actors = req.getParameter("actors");
        String durationParam = req.getParameter("duration");
        String description = req.getParameter("description");
        String posterUrl = req.getParameter("posterUrl");
        String trailerUrl = req.getParameter("trailerUrl");
        String releaseDateParam = req.getParameter("releaseDate");
        String ageRating = req.getParameter("ageRating");
        String status = req.getParameter("status");
        String genreIdParam = req.getParameter("genreId");

        String error = null;
        if (ValidationUtil.isBlank(title)) error = "Vui lòng nhập tên phim.";
        else if (ValidationUtil.isBlank(genreIdParam)) error = "Vui lòng chọn thể loại.";
        else if (ValidationUtil.isBlank(durationParam)) error = "Vui lòng nhập thời lượng.";

        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("genres", genreDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/admin/movie-form.jsp").forward(req, resp);
            return;
        }

        Movie m = new Movie();
        m.setTitle(title.trim());
        m.setDirector(director);
        m.setActors(actors);
        m.setDuration(Integer.parseInt(durationParam));
        m.setDescription(description);
        m.setPosterUrl(ValidationUtil.isBlank(posterUrl) ? "/assets/images/placeholder-poster.svg" : posterUrl.trim());
        m.setTrailerUrl(trailerUrl);
        m.setReleaseDate(ValidationUtil.isBlank(releaseDateParam) ? null : LocalDate.parse(releaseDateParam));
        m.setAgeRating(ValidationUtil.isBlank(ageRating) ? "P" : ageRating);
        m.setStatus(ValidationUtil.isBlank(status) ? Constants.MOVIE_COMING_SOON : status);
        m.setGenreId(Integer.parseInt(genreIdParam));

        if (idParam != null && !idParam.isEmpty()) {
            m.setId(Integer.parseInt(idParam));
            movieDAO.update(m);
        } else {
            movieDAO.insert(m);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/movies");
    }
}
