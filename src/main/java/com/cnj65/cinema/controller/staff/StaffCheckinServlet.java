package com.cnj65.cinema.controller.staff;

import com.cnj65.cinema.dao.TicketDAO;
import com.cnj65.cinema.model.Ticket;
import com.cnj65.cinema.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * CHECK-IN VE tai cua rap: Nhan vien nhap ma ve (hoac quet QR nhap ve o thanh
 * "id ma"), he thong kiem tra ve dang o trang thai PAID va suat chieu la HOM
 * NAY thi cho phep chuyen sang CHECKED_IN. Ve da CHECKED_IN roi khong the
 * check-in lai (chong gian lan dung 1 ve vao nhieu lan).
 */
@WebServlet("/staff/checkin")
public class StaffCheckinServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/staff/checkin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ticketCode = req.getParameter("ticketCode");

        if (ValidationUtil.isBlank(ticketCode)) {
            req.setAttribute("error", "Vui lòng nhập mã vé.");
            req.getRequestDispatcher("/WEB-INF/views/staff/checkin.jsp").forward(req, resp);
            return;
        }

        Ticket ticket = ticketDAO.findByCode(ticketCode.trim().toUpperCase());

        if (ticket == null) {
            req.setAttribute("error", "Không tìm thấy vé với mã: " + ticketCode);
        } else if ("CHECKED_IN".equals(ticket.getStatus())) {
            req.setAttribute("error", "Vé này đã được check-in trước đó lúc "
                    + (ticket.getCheckinAt() != null ? ticket.getCheckinAt() : "") + ".");
            req.setAttribute("ticket", ticket);
        } else if ("CANCELLED".equals(ticket.getStatus())) {
            req.setAttribute("error", "Vé này đã bị hủy, không thể check-in.");
            req.setAttribute("ticket", ticket);
        } else if (!"PAID".equals(ticket.getStatus())) {
            req.setAttribute("error", "Vé chưa được thanh toán, không thể check-in.");
            req.setAttribute("ticket", ticket);
        } else if (!ticket.getShowDate().equals(LocalDateTime.now().toLocalDate())) {
            req.setAttribute("error", "Vé này dành cho suất chiếu ngày " + ticket.getShowDateFormatted()
                    + ", không phải hôm nay.");
            req.setAttribute("ticket", ticket);
        } else {
            boolean ok = ticketDAO.checkin(ticket.getTicketCode());
            if (ok) {
                req.setAttribute("success", "Check-in thành công cho vé " + ticket.getTicketCode() + "!");
                req.setAttribute("ticket", ticketDAO.findByCode(ticket.getTicketCode()));
            } else {
                req.setAttribute("error", "Check-in thất bại, vui lòng thử lại.");
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/staff/checkin.jsp").forward(req, resp);
    }
}
