package com.cnj65.cinema.controller.admin;

import com.cnj65.cinema.dao.UserDAO;
import com.cnj65.cinema.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Quan ly Khach hang: tim kiem + khoa/mo khoa tai khoan (khong cho phep
 * khach hang vi pham dat ve nua, thay vi xoa han du lieu lich su).
 */
@WebServlet("/admin/customers")
public class AdminCustomerServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        req.setAttribute("customers", userDAO.searchCustomers(keyword));
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("/WEB-INF/views/admin/customer-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String action = req.getParameter("action");

        if ("lock".equals(action)) {
            userDAO.updateStatus(id, Constants.USER_LOCKED);
        } else if ("unlock".equals(action)) {
            userDAO.updateStatus(id, Constants.USER_ACTIVE);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/customers");
    }
}
