package com.cnj65.cinema.controller.staff;

import com.cnj65.cinema.dao.*;
import com.cnj65.cinema.model.Showtime;
import com.cnj65.cinema.model.ShowtimeSeat;
import com.cnj65.cinema.model.User;
import com.cnj65.cinema.util.Constants;
import com.cnj65.cinema.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * BAN VE TAI QUAY (nghiep vu Nhan vien) — khac voi Customer tu dat online:
 *   - Khong bat buoc khach phai co tai khoan (co the ban cho "khach le" bang
 *     ten/sdt nhap tay qua guestName/guestPhone).
 *   - Neu khach co tai khoan, Nhan vien co the tim theo SDT de gan ve vao
 *     lich su tai khoan cua khach (userId).
 *   - KHONG can qua buoc "giu ghe" nhu online — ghe duoc dat TRUC TIEP
 *     tu AVAILABLE -> BOOKED (xem ShowtimeSeatDAO.directBook), vi nhan vien
 *     thao tac tai quay, khong co do tre nhap lieu nhu khach hang online.
 *
 *   GET  /staff/sale                          -> man hinh chon phim/suat chieu
 *   GET  /staff/sale?action=seats&showtimeId= -> so do ghe de nhan vien chon
 *   POST /staff/sale (action=confirm)         -> tao ve, thanh toan CASH ngay
 */
@WebServlet("/staff/sale")
public class StaffSaleServlet extends HttpServlet {

    private final MovieDAO movieDAO = new MovieDAO();
    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private final ShowtimeSeatDAO showtimeSeatDAO = new ShowtimeSeatDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("seats".equals(action)) {
            int showtimeId = Integer.parseInt(req.getParameter("showtimeId"));
            Showtime showtime = showtimeDAO.findById(showtimeId);
            List<ShowtimeSeat> seats = showtimeSeatDAO.findByShowtime(showtimeId);

            Map<String, List<ShowtimeSeat>> seatsByRow = new LinkedHashMap<>();
            for (ShowtimeSeat s : seats) {
                String rowKey = s.getSeatCode().substring(0, 1);
                seatsByRow.computeIfAbsent(rowKey, k -> new java.util.ArrayList<>()).add(s);
            }

            req.setAttribute("showtime", showtime);
            req.setAttribute("seatsByRow", seatsByRow);

            String custPhone = req.getParameter("customerPhone");
            if (!ValidationUtil.isBlank(custPhone)) {
                req.setAttribute("foundCustomer", userDAO.findCustomerByPhone(custPhone));
            }

            req.getRequestDispatcher("/WEB-INF/views/staff/sale-seats.jsp").forward(req, resp);
            return;
        }

        // Man hinh chon phim dang chieu hom nay -> chon suat chieu
        req.setAttribute("nowShowingMovies", movieDAO.findByStatus(Constants.MOVIE_NOW_SHOWING));
        String movieIdParam = req.getParameter("movieId");
        if (!ValidationUtil.isBlank(movieIdParam)) {
            int movieId = Integer.parseInt(movieIdParam);
            req.setAttribute("selectedMovieId", movieId);
            req.setAttribute("showtimesToday", showtimeDAO.findByMovieAndDate(movieId, LocalDate.now()));
        }
        req.getRequestDispatcher("/WEB-INF/views/staff/sale.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User staff = (User) session.getAttribute(Constants.SESSION_USER);

        int showtimeId = Integer.parseInt(req.getParameter("showtimeId"));
        String[] seatIdParams = req.getParameterValues("seatId");
        String guestName = req.getParameter("guestName");
        String guestPhone = req.getParameter("guestPhone");
        String customerPhoneForAccount = req.getParameter("linkedCustomerId"); // id khach hang neu co tai khoan

        if (seatIdParams == null || seatIdParams.length == 0) {
            resp.sendRedirect(req.getContextPath() + "/staff/sale?action=seats&showtimeId=" + showtimeId
                    + "&error=no_seat_selected");
            return;
        }

        Showtime showtime = showtimeDAO.findById(showtimeId);
        List<ShowtimeSeat> allSeats = showtimeSeatDAO.findByShowtime(showtimeId);
        Map<Integer, BigDecimal> seatPrices = new HashMap<>();
        for (String sid : seatIdParams) {
            int seatId = Integer.parseInt(sid);
            allSeats.stream().filter(s -> s.getSeatId() == seatId).findFirst().ifPresent(s -> {
                seatPrices.put(seatId, priceForSeatType(showtime, s.getSeatType()));
            });
        }

        Integer linkedUserId = null;
        if (!ValidationUtil.isBlank(customerPhoneForAccount)) {
            try { linkedUserId = Integer.parseInt(customerPhoneForAccount); } catch (NumberFormatException ignored) {}
        }

        // Ban tai quay: userId = null neu khach le (dung guestName/guestPhone),
        // hoac gan userId neu khach co tai khoan da duoc Nhan vien tra ra.
        String ticketCode = ticketDAO.createTicket(
                linkedUserId, staff.getId(),
                linkedUserId == null ? guestName : null,
                linkedUserId == null ? guestPhone : null,
                showtimeId, seatPrices, Constants.PAY_CASH);

        if (ticketCode == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/sale?action=seats&showtimeId=" + showtimeId
                    + "&error=seats_taken");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/staff/sale-success?code=" + ticketCode);
    }

    private BigDecimal priceForSeatType(Showtime st, String seatType) {
        switch (seatType) {
            case "VIP": return st.getPriceVip();
            case "COUPLE": return st.getPriceCouple();
            default: return st.getPriceNormal();
        }
    }
}
