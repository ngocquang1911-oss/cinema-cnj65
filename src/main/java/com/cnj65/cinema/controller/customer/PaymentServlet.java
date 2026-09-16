package com.cnj65.cinema.controller.customer;

import com.cnj65.cinema.util.Constants;

import com.cnj65.cinema.dao.ShowtimeDAO;
import com.cnj65.cinema.dao.ShowtimeSeatDAO;
import com.cnj65.cinema.dao.TicketDAO;
import com.cnj65.cinema.model.Showtime;
import com.cnj65.cinema.model.ShowtimeSeat;
import com.cnj65.cinema.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * XU LY THANH TOAN va TAO VE (nghiep vu 17,18 cua Customer; LOGIC 5 trong ban goc).
 *
 * Trong do an, thanh toan online thuc (VNPay/Momo) co the duoc mo phong: chon
 * phuong thuc -> he thong coi nhu thanh toan thanh cong ngay (vi day la luong
 * quan trong nhat can chay dung, con tich hop cong thanh toan that la phan
 * co the nang cap them). Diem mau chot ve mat ky thuat la TOAN BO thao tac
 * (xac nhan ghe + tao ve + tao chi tiet ve + tao payment) nam trong 1
 * TRANSACTION duy nhat o TicketDAO.createTicket(), dam bao khong bao gio xay
 * ra tinh trang "da tao ve nhung ghe chua duoc khoa" hoac nguoc lai.
 */
@WebServlet("/booking/payment")
public class PaymentServlet extends HttpServlet {

    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private final ShowtimeSeatDAO showtimeSeatDAO = new ShowtimeSeatDAO();
    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_USER);

        int showtimeId = Integer.parseInt(req.getParameter("showtimeId"));
        String[] seatIdParams = req.getParameterValues("seatId");
        String paymentMethod = req.getParameter("paymentMethod");
        if (paymentMethod == null || paymentMethod.isEmpty()) paymentMethod = "CASH";

        Showtime showtime = showtimeDAO.findById(showtimeId);

        // Xay dung map seatId -> gia tien theo loai ghe (lay lai tu DB de dam bao
        // khong bi gia mao gia tien tu phia client)
        java.util.List<ShowtimeSeat> allSeats = showtimeSeatDAO.findByShowtime(showtimeId);
        Map<Integer, BigDecimal> seatPrices = new HashMap<>();
        for (String sid : seatIdParams) {
            int seatId = Integer.parseInt(sid);
            allSeats.stream().filter(s -> s.getSeatId() == seatId).findFirst().ifPresent(s -> {
                seatPrices.put(seatId, priceForSeatType(showtime, s.getSeatType()));
            });
        }

        String ticketCode = ticketDAO.createTicket(
                user.getId(), null, null, null, showtimeId, seatPrices, paymentMethod);

        if (ticketCode == null) {
            // Mot hoac nhieu ghe khong con hop le (het han giu ghe / bi nguoi khac dat truoc)
            resp.sendRedirect(req.getContextPath() + "/booking/seats?showtimeId=" + showtimeId
                    + "&error=seats_taken");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/ticket-success?code=" + ticketCode);
    }

    private BigDecimal priceForSeatType(Showtime st, String seatType) {
        switch (seatType) {
            case "VIP": return st.getPriceVip();
            case "COUPLE": return st.getPriceCouple();
            default: return st.getPriceNormal();
        }
    }
}
