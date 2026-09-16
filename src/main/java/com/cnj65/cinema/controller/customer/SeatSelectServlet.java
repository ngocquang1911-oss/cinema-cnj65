package com.cnj65.cinema.controller.customer;

import com.cnj65.cinema.dao.ShowtimeDAO;
import com.cnj65.cinema.dao.ShowtimeSeatDAO;
import com.cnj65.cinema.model.Showtime;
import com.cnj65.cinema.model.ShowtimeSeat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Hien thi so do ghe cua 1 suat chieu (nghiep vu 11,12 cua Customer).
 * URL nam trong /booking/* nen AuthFilter se bat buoc dang nhap truoc
 * (dung LOGIC 1 trong ban nghiep vu goc).
 */
@WebServlet("/booking/seats")
public class SeatSelectServlet extends HttpServlet {

    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private final ShowtimeSeatDAO showtimeSeatDAO = new ShowtimeSeatDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int showtimeId;
        try {
            showtimeId = Integer.parseInt(req.getParameter("showtimeId"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/movies");
            return;
        }

        Showtime showtime = showtimeDAO.findById(showtimeId);
        if (showtime == null || "CANCELLED".equals(showtime.getStatus())) {
            req.setAttribute("error", "Suat chieu khong con hieu luc.");
            req.getRequestDispatcher("/WEB-INF/views/customer/movie-list.jsp").forward(req, resp);
            return;
        }

        List<ShowtimeSeat> seats = showtimeSeatDAO.findByShowtime(showtimeId);

        // Nhom ghe theo tung hang (A, B, C...) de JSP render dang luoi ma khong
        // can viet logic gom nhom trong JSTL (JSTL khong co ham groupBy san).
        // LinkedHashMap giu dung thu tu hang vi DAO da ORDER BY seat_row.
        Map<String, List<ShowtimeSeat>> seatsByRow = new LinkedHashMap<>();
        for (ShowtimeSeat s : seats) {
            String rowKey = s.getSeatCode().substring(0, 1); // "A01" -> "A"
            seatsByRow.computeIfAbsent(rowKey, k -> new java.util.ArrayList<>()).add(s);
        }

        req.setAttribute("showtime", showtime);
        req.setAttribute("seatsByRow", seatsByRow);
        req.getRequestDispatcher("/WEB-INF/views/customer/seat-select.jsp").forward(req, resp);
    }
}

