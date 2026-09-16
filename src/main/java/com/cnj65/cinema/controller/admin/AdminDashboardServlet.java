package com.cnj65.cinema.controller.admin;

import com.cnj65.cinema.dao.TicketDAO;
import com.cnj65.cinema.util.FormatUtil;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Trang tong quan cho Admin: doanh thu, so ve da ban, top phim ban chay,
 * bieu do doanh thu 7 ngay gan nhat (du lieu JSON de JS ve bieu do - dung
 * phuong an C: JSP mong + JS xu ly phan tuong tac/truc quan).
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        BigDecimal totalRevenue = ticketDAO.totalRevenue();
        int totalTickets = ticketDAO.totalTicketsSold();
        List<Object[]> revenueByDay = ticketDAO.revenueByDay(7);
        List<Object[]> topMovies = ticketDAO.topMoviesBySales(5);

        // Chuyen sang dang JSON goi ngay duoc cho Chart.js/JS thuan phia frontend
        List<String> chartLabels = new ArrayList<>();
        List<BigDecimal> chartValues = new ArrayList<>();
        for (Object[] row : revenueByDay) {
            chartLabels.add(row[0].toString());
            chartValues.add((BigDecimal) row[1]);
        }

        req.setAttribute("totalRevenueFormatted", FormatUtil.currency(totalRevenue));
        req.setAttribute("totalTickets", totalTickets);
        req.setAttribute("topMovies", topMovies);
        req.setAttribute("chartLabelsJson", gson.toJson(chartLabels));
        req.setAttribute("chartValuesJson", gson.toJson(chartValues));

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
