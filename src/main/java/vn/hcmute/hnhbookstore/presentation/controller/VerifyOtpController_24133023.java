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

@WebServlet(urlPatterns = {"/verify-otp", "/resend-otp"})
public final class VerifyOtpController_24133023 extends HttpServlet {
    private final AuthService_24133023 authService = new AuthService_24133023();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        OtpSessionData_24133023 otpData = (session != null)
                ? (OtpSessionData_24133023) session.getAttribute("otpData")
                : null;

        if (otpData == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        CsrfUtil_24133023.getOrCreateToken(session);
        request.setAttribute("otpData", otpData);
        request.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        if (!CsrfUtil_24133023.validateToken(request)) {
            request.setAttribute("errorMessage", "Yêu cầu không hợp lệ hoặc phiên bảo mật đã hết hạn.");
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp").forward(request, response);
            return;
        }

        OtpSessionData_24133023 otpData = (OtpSessionData_24133023) session.getAttribute("otpData");
        if (otpData == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        String path = request.getServletPath();

        if ("/resend-otp".equals(path)) {
            try {
                authService.resendOtp(otpData);
                request.setAttribute("successMessage", "Mã OTP mới đã được gửi thành công đến email: " + otpData.getEmail());
            } catch (AuthException_24133023 e) {
                request.setAttribute("errorMessage", e.getMessage());
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Không thể gửi lại mã OTP: " + e.getMessage());
            }
            request.setAttribute("otpData", otpData);
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp").forward(request, response);
            return;
        }

        // Verify OTP branch
        String inputOtp = request.getParameter("otp");
        try {
            authService.verifyOtp(otpData, inputOtp);
            // OTP verification successful! Clear pending OTP session
            session.removeAttribute("otpData");
            response.sendRedirect(request.getContextPath() + "/login?verified=1");
        } catch (AuthException_24133023 e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("otpData", otpData);
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Lỗi hệ thống khi xác thực OTP: " + e.getMessage());
            request.setAttribute("otpData", otpData);
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-otp.jsp").forward(request, response);
        }
    }
}

