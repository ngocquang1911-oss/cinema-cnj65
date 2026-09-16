package com.cnj65.cinema.controller.auth;

import com.cnj65.cinema.util.Constants;

import com.cnj65.cinema.dao.UserDAO;
import com.cnj65.cinema.model.User;
import com.cnj65.cinema.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Neu da dang nhap roi thi khong can vao trang login nua
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constants.SESSION_USER) != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = userDAO.findByUsername(username);

        if (user == null || !PasswordUtil.verify(password, user.getPassword())) {
            req.setAttribute("error", "Ten dang nhap hoac mat khau khong dung.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        if ("LOCKED".equals(user.getStatus())) {
            req.setAttribute("error", "Tai khoan cua ban da bi khoa. Vui long lien he quan tri vien.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        // Dang nhap thanh cong -> tao Session
        HttpSession session = req.getSession(true);
        session.setAttribute(Constants.SESSION_USER, user);
        session.setMaxInactiveInterval(30 * 60); // 30 phut

        // Dieu huong theo vai tro
        String redirect = (String) session.getAttribute(Constants.SESSION_REDIRECT_AFTER_LOGIN);
        session.removeAttribute(Constants.SESSION_REDIRECT_AFTER_LOGIN);

        if (redirect != null && !redirect.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + redirect);
        } else if (user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else if (user.isStaff()) {
            resp.sendRedirect(req.getContextPath() + "/staff/sale");
        } else {
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }
}
