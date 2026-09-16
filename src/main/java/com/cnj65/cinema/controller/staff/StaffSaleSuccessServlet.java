package com.cnj65.cinema.controller.staff;

import com.cnj65.cinema.dao.TicketDAO;
import com.cnj65.cinema.model.Ticket;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Man hinh xac nhan sau khi Nhan vien ban ve tai quay thanh cong */
@WebServlet("/staff/sale-success")
public class StaffSaleSuccessServlet extends HttpServlet {

    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String code = req.getParameter("code");
        Ticket ticket = ticketDAO.findByCode(code);
        if (ticket == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/sale");
            return;
        }
        req.setAttribute("ticket", ticket);
        req.getRequestDispatcher("/WEB-INF/views/staff/sale-success.jsp").forward(req, resp);
    }
}
