package com.cnj65.cinema.controller.admin;

import com.cnj65.cinema.dao.UserDAO;
import com.cnj65.cinema.model.User;
import com.cnj65.cinema.util.Constants;
import com.cnj65.cinema.util.PasswordUtil;
import com.cnj65.cinema.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Quan ly tai khoan Nhan vien: Admin tao tai khoan cho Nhan vien (Nhan vien
 * KHONG tu dang ky duoc — day la diem khac biet quan trong voi Customer,
 * dam bao chi Admin moi cap phat quyen ban ve/check-in).
 */
@WebServlet("/admin/staff")
public class AdminStaffServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("staffList", userDAO.findAllStaff());
        req.getRequestDispatcher("/WEB-INF/views/admin/staff-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("lock".equals(action) || "unlock".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            userDAO.updateStatus(id, "lock".equals(action) ? Constants.USER_LOCKED : Constants.USER_ACTIVE);
            resp.sendRedirect(req.getContextPath() + "/admin/staff");
            return;
        }

        // action=add (tao tai khoan nhan vien moi)
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        String error = null;
        if (!ValidationUtil.isValidUsername(username)) error = "Tên đăng nhập phải 4-30 ký tự, chỉ gồm chữ/số/gạch dưới.";
        else if (!ValidationUtil.isValidPassword(password)) error = "Mật khẩu phải có ít nhất 6 ký tự.";
        else if (ValidationUtil.isBlank(fullName)) error = "Vui lòng nhập họ tên.";
        else if (!ValidationUtil.isValidEmail(email)) error = "Email không hợp lệ.";
        else if (!ValidationUtil.isValidPhone(phone)) error = "Số điện thoại không hợp lệ.";
        else if (userDAO.existsByUsernameOrEmail(username, email)) error = "Tên đăng nhập hoặc email đã tồn tại.";

        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("staffList", userDAO.findAllStaff());
            req.getRequestDispatcher("/WEB-INF/views/admin/staff-list.jsp").forward(req, resp);
            return;
        }

        User u = new User();
        u.setUsername(username.trim());
        u.setPassword(PasswordUtil.hash(password));
        u.setFullName(fullName.trim());
        u.setEmail(email.trim());
        u.setPhone(phone.trim());
        userDAO.createStaff(u);

        resp.sendRedirect(req.getContextPath() + "/admin/staff?success=created");
    }
}
