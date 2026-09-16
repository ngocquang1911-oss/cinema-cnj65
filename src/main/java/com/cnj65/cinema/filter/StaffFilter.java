package com.cnj65.cinema.filter;

import com.cnj65.cinema.model.User;
import com.cnj65.cinema.util.Constants;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** STAFF hoac ADMIN moi duoc truy cap khu vuc /staff/* (ban ve quay, check-in) */
@WebFilter("/staff/*")
public class StaffFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(Constants.SESSION_USER) : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?notice=login_required");
            return;
        }
        if (!user.isStaff() && !user.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Ban khong co quyen truy cap trang nay");
            return;
        }
        chain.doFilter(req, res);
    }
}
