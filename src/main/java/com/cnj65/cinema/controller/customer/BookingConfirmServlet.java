package com.cnj65.cinema.controller.customer;

import com.cnj65.cinema.util.Constants;

import com.cnj65.cinema.dao.ShowtimeDAO;
import com.cnj65.cinema.dao.ShowtimeSeatDAO;
import com.cnj65.cinema.model.Showtime;
import com.cnj65.cinema.model.ShowtimeSeat;
import com.cnj65.cinema.model.User;
import com.cnj65.cinema.util.FormatUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Trang XAC NHAN DON HANG (nghiep vu 15,16 cua Customer: Tinh tien + Xac nhan).
 * Nhan danh sach ghe da giu (seatIds) tu trang chon ghe, tinh tong tien theo
 * loai ghe (LOGIC 4 trong ban nghiep vu goc), hien thi truoc khi thanh toan.
 */
@WebServlet("/booking/confirm")
public class BookingConfirmServlet extends HttpServlet {

    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private final ShowtimeSeatDAO showtimeSeatDAO = new ShowtimeSeatDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_USER);

        int showtimeId;
        String[] seatIdParams = req.getParameterValues("seatId");
        try {
            showtimeId = Integer.parseInt(req.getParameter("showtimeId"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/movies");
            return;
        }
        if (seatIdParams == null || seatIdParams.length == 0) {
            resp.sendRedirect(req.getContextPath() + "/booking/seats?showtimeId=" + showtimeId
                    + "&error=no_seat_selected");
            return;
        }

        Showtime showtime = showtimeDAO.findById(showtimeId);
        List<ShowtimeSeat> allSeats = showtimeSeatDAO.findByShowtime(showtimeId);

        // Chi lay nhung ghe dang LOCKED boi CHINH nguoi dung nay (bao mat: khong cho
        // "gian lan" bang cach truyen tay seatId cua ghe minh chua giu)
        List<ShowtimeSeat> selectedSeats = new ArrayList<>();
        for (String sid : seatIdParams) {
            int seatId = Integer.parseInt(sid);
            allSeats.stream()
                    .filter(s -> s.getSeatId() == seatId
                            && "LOCKED".equals(s.getStatus())
                            && user.getId() == (s.getLockedBy() == null ? -1 : s.getLockedBy()))
                    .findFirst()
                    .ifPresent(selectedSeats::add);
        }

        if (selectedSeats.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/booking/seats?showtimeId=" + showtimeId
                    + "&error=seats_expired");
            return;
        }

        // LOGIC 4: Tinh tien theo loai ghe
        BigDecimal total = BigDecimal.ZERO;
        for (ShowtimeSeat s : selectedSeats) {
            total = total.add(priceForSeatType(showtime, s.getSeatType()));
        }

        req.setAttribute("showtime", showtime);
        req.setAttribute("selectedSeats", selectedSeats);
        req.setAttribute("total", total);
        req.setAttribute("totalFormatted", FormatUtil.currency(total));
        req.getRequestDispatcher("/WEB-INF/views/customer/booking-confirm.jsp").forward(req, resp);
    }

    private BigDecimal priceForSeatType(Showtime st, String seatType) {
        switch (seatType) {
            case "VIP": return st.getPriceVip();
            case "COUPLE": return st.getPriceCouple();
            default: return st.getPriceNormal();
        }
    }
}
