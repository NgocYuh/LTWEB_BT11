<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đăng ký tài khoản</title>
</head>
<body>
<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <p class="eyebrow">HNHBOOKSTORE / XÁC THỰC</p>
            <h1>Đăng ký tài khoản</h1>
            <p>Tạo tài khoản mới để trải nghiệm đọc và đánh giá sách</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error" role="alert">
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/register" novalidate>
            <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">

            <div class="form-group">
                <label class="form-label" for="email">Địa chỉ Email <span style="color:var(--danger)">*</span></label>
                <input class="form-input" type="email" id="email" name="email" maxlength="50" required
                       value="<c:out value='${formEmail}'/>" placeholder="vi-du: yourname@gmail.com">
                <span class="form-help">Mã OTP xác thực sẽ được gửi đến email này (tối đa 50 ký tự).</span>
            </div>

            <div class="form-group">
                <label class="form-label" for="fullname">Họ và tên <span style="color:var(--danger)">*</span></label>
                <input class="form-input" type="text" id="fullname" name="fullname" maxlength="50" required
                       value="<c:out value='${formFullname}'/>" placeholder="Nguyễn Văn A">
            </div>

            <div class="form-group">
                <label class="form-label" for="phone">Số điện thoại <small class="muted">(tùy chọn)</small></label>
                <input class="form-input" type="text" id="phone" name="phone"
                       value="<c:out value='${formPhone}'/>" placeholder="24133023">
                <span class="form-help">Giới hạn kiểu INT của SQL Server (tối đa 2.147.483.647, không hỗ trợ số 0 đầu).</span>
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Mật khẩu <span style="color:var(--danger)">*</span></label>
                <input class="form-input" type="password" id="password" name="password" minlength="6" required
                       placeholder="Ít nhất 6 ký tự">
                <span class="form-help">Mật khẩu được băm bảo mật bằng chuẩn PBKDF2-HMAC-SHA256 với salt riêng.</span>
            </div>

            <div class="form-group">
                <label class="form-label" for="confirmPassword">Nhập lại mật khẩu <span style="color:var(--danger)">*</span></label>
                <input class="form-input" type="password" id="confirmPassword" name="confirmPassword" minlength="6" required
                       placeholder="Nhập lại chính xác mật khẩu">
            </div>

            <div class="form-actions">
                <button type="submit" class="button button-primary btn-block">Tiếp tục — Nhận mã OTP</button>
            </div>
        </form>

        <div class="auth-links">
            Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập tại đây</a>
        </div>
    </div>
</div>
</body>
</html>

