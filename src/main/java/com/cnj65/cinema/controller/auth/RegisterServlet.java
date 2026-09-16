package com.cnj65.cinema.controller.auth;

import com.cnj65.cinema.dao.UserDAO;
import com.cnj65.cinema.model.User;
import com.cnj65.cinema.util.PasswordUtil;
import com.cnj65.cinema.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        // ==== VALIDATE DU LIEU DAU VAO ====
        String error = null;
        if (ValidationUtil.isBlank(fullName)) {
            error = "Vui long nhap ho ten.";
        } else if (!ValidationUtil.isValidUsername(username)) {
            error = "Ten dang nhap phai tu 4-30 ky tu, chi gom chu, so, gach duoi.";
        } else if (!ValidationUtil.isValidPassword(password)) {
            error = "Mat khau phai co it nhat 6 ky tu.";
        } else if (!password.equals(confirmPassword)) {
            error = "Mat khau xac nhan khong khop.";
        } else if (!ValidationUtil.isValidEmail(email)) {
            error = "Email khong hop le.";
        } else if (!ValidationUtil.isValidPhone(phone)) {
            error = "So dien thoai khong hop le (vi du: 0912345678).";
        } else if (userDAO.existsByUsernameOrEmail(username, email)) {
            error = "Ten dang nhap hoac email da ton tai.";
        }

        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("fullName", fullName);
            req.setAttribute("username", username);
            req.setAttribute("email", email);
            req.setAttribute("phone", phone);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        User u = new User();
        u.setUsername(username.trim());
        u.setPassword(PasswordUtil.hash(password));
        u.setFullName(fullName.trim());
        u.setEmail(email.trim());
        u.setPhone(phone.trim());

        userDAO.register(u);

        resp.sendRedirect(req.getContextPath() + "/login?notice=register_success");
    }
}
