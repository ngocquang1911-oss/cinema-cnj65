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
import java.util.List;

/** "Ve cua toi" + Lich su dat ve (nghiep vu 19,21 cua Customer) */
@WebServlet("/booking/my-tickets")
public class MyTicketsServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_USER);

        List<Ticket> tickets = ticketDAO.findByUser(user.getId());
        req.setAttribute("tickets", tickets);
        req.getRequestDispatcher("/WEB-INF/views/customer/my-tickets.jsp").forward(req, resp);
    }
}
