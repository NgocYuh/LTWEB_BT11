package vn.hcmute.hnhbookstore.presentation.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.hcmute.hnhbookstore.model.User_24133023;

import java.io.IOException;

public final class AdminAuthFilter_24133023 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User_24133023 currentUser = (session != null)
                ? (User_24133023) session.getAttribute("currentUser")
                : null;

        if (currentUser == null) {
            // Guest -> redirect to login page
            res.sendRedirect(req.getContextPath() + "/login?required=1");
            return;
        }

        if (!currentUser.isAdmin()) {
            // Logged in user but not an admin -> 403 Forbidden
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập khu vực quản trị.");
            return;
        }

        chain.doFilter(request, response);
    }
}

