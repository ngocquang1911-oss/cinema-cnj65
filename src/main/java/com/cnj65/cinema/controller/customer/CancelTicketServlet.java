package com.cnj65.cinema.controller.customer;

import com.cnj65.cinema.util.Constants;

import com.cnj65.cinema.dao.TicketDAO;
import com.cnj65.cinema.model.Ticket;
import com.cnj65.cinema.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

/** Huy ve (nghiep vu 22 cua Customer) - chi cho huy truoc gio chieu >= 2 tieng */
@WebServlet("/booking/cancel-ticket")
public class CancelTicketServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_USER);

        int ticketId = Integer.parseInt(req.getParameter("id"));
        Ticket ticket = ticketDAO.findById(ticketId);

        if (ticket == null || ticket.getUserId() == null || ticket.getUserId() != user.getId()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        LocalDateTime showDateTime = LocalDateTime.of(ticket.getShowDate(), ticket.getStartTime());
        if (showDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
            resp.sendRedirect(req.getContextPath() + "/booking/ticket-detail?id=" + ticketId
                    + "&error=too_late_to_cancel");
            return;
        }

        boolean ok = ticketDAO.cancelTicket(ticketId);
        resp.sendRedirect(req.getContextPath() + "/booking/ticket-detail?id=" + ticketId
                + (ok ? "&success=cancelled" : "&error=cancel_failed"));
    }
}
