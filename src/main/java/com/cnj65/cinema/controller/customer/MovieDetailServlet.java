package com.cnj65.cinema.controller.customer;

import com.cnj65.cinema.dao.MovieDAO;
import com.cnj65.cinema.dao.ShowtimeDAO;
import com.cnj65.cinema.model.Movie;
import com.cnj65.cinema.model.Showtime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Xem chi tiet phim + lich chieu theo ngay (nghiep vu 9,10 cua Customer) */
@WebServlet("/movie-detail")
public class MovieDetailServlet extends HttpServlet {

    private final MovieDAO movieDAO = new MovieDAO();
    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd/MM");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int movieId;
        try {
            movieId = Integer.parseInt(req.getParameter("id"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/movies");
            return;
        }

        Movie movie = movieDAO.findById(movieId);
        if (movie == null) {
            resp.sendRedirect(req.getContextPath() + "/movies");
            return;
        }

        String dateParam = req.getParameter("date");
        LocalDate selectedDate = (dateParam != null && !dateParam.isEmpty())
                ? LocalDate.parse(dateParam)
                : LocalDate.now();

        List<Showtime> showtimes = showtimeDAO.findByMovieAndDate(movieId, selectedDate);

        // Sinh danh sach 7 ngay ke tu hom nay de JSP render thanh tab chon nhanh.
        // key = nhan hien thi ("Hôm nay","Ngày mai","dd/MM"), value = chuoi yyyy-MM-dd
        // dung lam tham so URL.
        Map<String, String> dateOptions = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate d = today.plusDays(i);
            String label = (i == 0) ? "Hôm nay" : (i == 1) ? "Ngày mai" : d.format(SHORT_DATE);
            dateOptions.put(label, d.toString());
        }

        req.setAttribute("movie", movie);
        req.setAttribute("showtimes", showtimes);
        req.setAttribute("selectedDate", selectedDate.toString());
        req.setAttribute("dateOptions", dateOptions);
        req.setAttribute("todayStr", today.toString()); // dung lam gia tri "min" cho o chon ngay bat ky ben JSP
        req.getRequestDispatcher("/WEB-INF/views/customer/movie-detail.jsp").forward(req, resp);
    }
}