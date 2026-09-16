package com.cnj65.cinema.controller.customer;

import com.cnj65.cinema.dao.TicketDAO;
import com.cnj65.cinema.model.Ticket;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Hien thi ve dien tu sau khi dat thanh cong (nghiep vu 19,20) */
@WebServlet("/ticket-success")
public class TicketSuccessServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String code = req.getParameter("code");
        Ticket ticket = ticketDAO.findByCode(code);

        if (ticket == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("ticket", ticket);
        req.getRequestDispatcher("/WEB-INF/views/customer/ticket-success.jsp").forward(req, resp);
    }
}
