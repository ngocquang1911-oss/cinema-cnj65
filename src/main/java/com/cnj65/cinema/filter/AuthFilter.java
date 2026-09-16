package com.cnj65.cinema.filter;

import com.cnj65.cinema.model.User;
import com.cnj65.cinema.util.Constants;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Bat buoc dang nhap moi duoc truy cap (dung cho luong Dat ve, Ve cua toi...).
 * LOGIC 1 trong ban nghiep vu goc: "Chua dang nhap khong duoc dat ve"
 */
@WebFilter("/booking/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(Constants.SESSION_USER) : null;

        if (user == null) {
            // Luu lai URL dang truy cap de sau khi login xong quay lai dung cho
            String targetUrl = request.getRequestURI();
            if (request.getQueryString() != null) targetUrl += "?" + request.getQueryString();
            request.getSession().setAttribute(Constants.SESSION_REDIRECT_AFTER_LOGIN, targetUrl);
            response.sendRedirect(request.getContextPath() + "/login?notice=login_required");
            return;
        }
        chain.doFilter(req, res);
    }
}
