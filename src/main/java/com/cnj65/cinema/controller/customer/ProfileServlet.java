package com.cnj65.cinema.controller.customer;

import com.cnj65.cinema.dao.UserDAO;
import com.cnj65.cinema.model.User;
import com.cnj65.cinema.util.Constants;
import com.cnj65.cinema.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** Xem / cap nhat thong tin ca nhan (nghiep vu 4,5 cua Customer) */
@WebServlet("/account/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User sessionUser = (User) session.getAttribute(Constants.SESSION_USER);
        User freshUser = userDAO.findById(sessionUser.getId());
        req.setAttribute("profileUser", freshUser);
        req.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User sessionUser = (User) session.getAttribute(Constants.SESSION_USER);

        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        String error = null;
        if (ValidationUtil.isBlank(fullName)) error = "Vui long nhap ho ten.";
        else if (!ValidationUtil.isValidEmail(email)) error = "Email khong hop le.";
        else if (!ValidationUtil.isValidPhone(phone)) error = "So dien thoai khong hop le.";

        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("profileUser", sessionUser);
            req.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(req, resp);
            return;
        }

        userDAO.updateProfile(sessionUser.getId(), fullName, email, phone);

        // Cap nhat lai thong tin trong Session cho dong bo
        sessionUser.setFullName(fullName);
        sessionUser.setEmail(email);
        sessionUser.setPhone(phone);
        session.setAttribute(Constants.SESSION_USER, sessionUser);

        req.setAttribute("success", "Cap nhat thong tin thanh cong.");
        req.setAttribute("profileUser", sessionUser);
        req.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(req, resp);
    }
}
