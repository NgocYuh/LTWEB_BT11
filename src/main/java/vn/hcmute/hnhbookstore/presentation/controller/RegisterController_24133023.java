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
import vn.hcmute.hnhbookstore.util.CsrfUtil_24133023;

import java.io.IOException;

@WebServlet(urlPatterns = {"/register"})
public final class RegisterController_24133023 extends HttpServlet {
    private final AuthService_24133023 authService = new AuthService_24133023();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        CsrfUtil_24133023.getOrCreateToken(session);
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil_24133023.validateToken(request)) {
            request.setAttribute("errorMessage", "Yêu cầu không hợp lệ hoặc phiên bảo mật đã hết hạn. Vui lòng thử lại.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        String email = request.getParameter("email");
        String fullname = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Keep form inputs for re-filling on error
        request.setAttribute("formEmail", email);
        request.setAttribute("formFullname", fullname);
        request.setAttribute("formPhone", phone);

        try {
            OtpSessionData_24133023 otpData = authService.register(email, fullname, phone, password, confirmPassword);
            HttpSession session = request.getSession(true);
            session.setAttribute("otpData", otpData);
            response.sendRedirect(request.getContextPath() + "/verify-otp");
        } catch (AuthException_24133023 e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
}

