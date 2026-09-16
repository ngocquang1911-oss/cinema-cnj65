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

/** Chi tiet 1 ve (nghiep vu 20) */
@WebServlet("/booking/ticket-detail")
public class TicketDetailServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_USER);

        int ticketId = Integer.parseInt(req.getParameter("id"));
        Ticket ticket = ticketDAO.findById(ticketId);

        // Bao mat: chi cho xem ve cua chinh minh
        if (ticket == null || ticket.getUserId() == null || ticket.getUserId() != user.getId()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Ban khong co quyen xem ve nay");
            return;
        }

        req.setAttribute("ticket", ticket);
        req.getRequestDispatcher("/WEB-INF/views/customer/ticket-detail.jsp").forward(req, resp);
    }
}
