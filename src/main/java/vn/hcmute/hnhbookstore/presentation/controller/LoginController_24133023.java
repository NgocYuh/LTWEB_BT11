package vn.hcmute.hnhbookstore.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.hcmute.hnhbookstore.business.AuthException_24133023;
import vn.hcmute.hnhbookstore.business.AuthService_24133023;
import vn.hcmute.hnhbookstore.model.OtpSessionData_24133023;
import vn.hcmute.hnhbookstore.model.User_24133023;
import vn.hcmute.hnhbookstore.util.CsrfUtil_24133023;

import java.io.IOException;

@WebServlet(urlPatterns = {"/login"})
public final class LoginController_24133023 extends HttpServlet {
    private final AuthService_24133023 authService = new AuthService_24133023();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User_24133023 currentUser = (User_24133023) session.getAttribute("currentUser");
            if (currentUser != null) {
                if (currentUser.isAdmin()) {
                    response.sendRedirect(request.getContextPath() + "/admin/books");
                } else {
                    response.sendRedirect(request.getContextPath() + "/home");
                }
                return;
            }
        }

        HttpSession currentSession = request.getSession(true);
        CsrfUtil_24133023.getOrCreateToken(currentSession);

        if ("1".equals(request.getParameter("verified"))) {
            request.setAttribute("successMessage", "Xác thực tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
        } else if ("1".equals(request.getParameter("logged_out"))) {
            request.setAttribute("infoMessage", "Bạn đã đăng xuất an toàn khỏi hệ thống.");
        } else if ("1".equals(request.getParameter("required"))) {
            request.setAttribute("errorMessage", "Vui lòng đăng nhập để truy cập chức năng này.");
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil_24133023.validateToken(request)) {
            request.setAttribute("errorMessage", "Phiên bảo mật đã hết hạn hoặc yêu cầu không hợp lệ. Vui lòng thử lại.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        request.setAttribute("formEmail", email);

        try {
            User_24133023 user = authService.login(email, password);

            // Rotate session ID to prevent Session Fixation attack
            request.changeSessionId();
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            // Re-generate fresh CSRF token for the authenticated session
            session.removeAttribute(CsrfUtil_24133023.CSRF_SESSION_ATTR);
            CsrfUtil_24133023.getOrCreateToken(session);

            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/books");
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } catch (AuthException_24133023 e) {
            request.setAttribute("errorMessage", e.getMessage());
            // If user exists but is unverified, allow them to initiate OTP verification directly
            if (e.getMessage() != null && e.getMessage().contains("chưa được kích hoạt")) {
                request.setAttribute("showVerifyLink", true);
                request.setAttribute("unverifiedEmail", email);
            }
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi đăng nhập: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}

