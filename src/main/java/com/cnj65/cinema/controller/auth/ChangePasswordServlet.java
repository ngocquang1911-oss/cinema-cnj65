package com.cnj65.cinema.controller.auth;

import com.cnj65.cinema.util.Constants;

import com.cnj65.cinema.dao.UserDAO;
import com.cnj65.cinema.model.User;
import com.cnj65.cinema.util.PasswordUtil;
import com.cnj65.cinema.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/** Doi mat khau - chuc nang thuong bi thieu trong cac do an, nhung rat quan trong */
@WebServlet("/account/change-password")
public class ChangePasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/customer/change-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User sessionUser = (User) session.getAttribute(Constants.SESSION_USER);

        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        User dbUser = userDAO.findById(sessionUser.getId());
        String error = null;

        if (!PasswordUtil.verify(oldPassword, dbUser.getPassword())) {
            error = "Mat khau cu khong dung.";
        } else if (!ValidationUtil.isValidPassword(newPassword)) {
            error = "Mat khau moi phai co it nhat 6 ky tu.";
        } else if (!newPassword.equals(confirmPassword)) {
            error = "Xac nhan mat khau moi khong khop.";
        }

        if (error != null) {
            req.setAttribute("error", error);
            req.getRequestDispatcher("/WEB-INF/views/customer/change-password.jsp").forward(req, resp);
            return;
        }

        userDAO.changePassword(sessionUser.getId(), PasswordUtil.hash(newPassword));
        req.setAttribute("success", "Doi mat khau thanh cong.");
        req.getRequestDispatcher("/WEB-INF/views/customer/change-password.jsp").forward(req, resp);
    }
}
