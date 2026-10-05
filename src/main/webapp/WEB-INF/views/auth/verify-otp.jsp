<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Xác thực mã OTP</title>
</head>
<body>
<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <p class="eyebrow">HNHBOOKSTORE / XÁC THỰC EMAIL</p>
            <h1>Nhập mã OTP</h1>
            <p>Mã xác thực 6 chữ số đã được gửi đến hộp thư:</p>
            <p style="font-weight:700;color:var(--blue);margin-top:6px;"><c:out value="${otpData.email}"/></p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error" role="alert">
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success" role="alert">
                <c:out value="${successMessage}"/>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/verify-otp">
            <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">

            <div class="form-group">
                <label class="form-label" for="otp" style="text-align:center;">Mã xác thực OTP (6 số)</label>
                <input class="form-input otp-input" type="text" id="otp" name="otp" maxlength="6"
                       pattern="\d{6}" inputmode="numeric" autocomplete="one-time-code" required autofocus
                       placeholder="••••••">
                <span class="form-help" style="text-align:center;">
                    Mã có hiệu lực trong 5 phút. Bạn còn <strong><c:out value="${otpData.attempts}"/></strong> lần thử.
                </span>
            </div>

            <div class="form-actions">
                <button type="submit" class="button button-primary btn-block">Xác nhận kích hoạt tài khoản</button>
            </div>
        </form>

        <div style="margin-top:20px;padding-top:20px;border-top:1px solid var(--line);text-align:center;">
            <p style="font-size:13px;color:var(--muted);margin-bottom:12px;">Chưa nhận được email hoặc mã hết hạn?</p>
            <form method="post" action="${pageContext.request.contextPath}/resend-otp">
                <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
                <button type="submit" class="button button-outline btn-block">Gửi lại mã OTP</button>
            </form>
        </div>

        <div class="auth-links">
            Nhầm lẫn thông tin? <a href="${pageContext.request.contextPath}/register">Đăng ký lại</a>
        </div>
    </div>
</div>
</body>
</html>

