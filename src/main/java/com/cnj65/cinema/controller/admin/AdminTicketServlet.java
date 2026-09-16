package com.cnj65.cinema.controller.admin;

import com.cnj65.cinema.dao.TicketDAO;
import com.cnj65.cinema.model.Ticket;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Quan ly Ve (xem toan bo he thong, loc theo trang thai, huy ve khi can) */
@WebServlet("/admin/tickets")
public class AdminTicketServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("detail".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Ticket ticket = ticketDAO.findById(id);
            if (ticket == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/tickets");
                return;
            }
            req.setAttribute("ticket", ticket);
            req.getRequestDispatcher("/WEB-INF/views/admin/ticket-detail.jsp").forward(req, resp);
            return;
        }

        String status = req.getParameter("status");
        req.setAttribute("tickets", ticketDAO.findAll(status));
        req.setAttribute("selectedStatus", status);
        req.getRequestDispatcher("/WEB-INF/views/admin/ticket-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        ticketDAO.cancelTicket(id);
        resp.sendRedirect(req.getContextPath() + "/admin/tickets?action=detail&id=" + id + "&success=cancelled");
    }
}
