<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đăng nhập</title>
</head>
<body>
<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <p class="eyebrow">HNHBOOKSTORE / XÁC THỰC</p>
            <h1>Đăng nhập</h1>
            <p>Chào mừng bạn quay trở lại với HNHBOOKSTORE</p>
        </div>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success" role="alert">
                <c:out value="${successMessage}"/>
            </div>
        </c:if>

        <c:if test="${not empty infoMessage}">
            <div class="alert alert-info" role="alert">
                <c:out value="${infoMessage}"/>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error" role="alert">
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login" novalidate>
            <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">

            <div class="form-group">
                <label class="form-label" for="email">Địa chỉ Email</label>
                <input class="form-input" type="email" id="email" name="email" maxlength="50" required autofocus
                       value="<c:out value='${formEmail}'/>" placeholder="name@example.com">
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Mật khẩu</label>
                <input class="form-input" type="password" id="password" name="password" required
                       placeholder="Nhập mật khẩu của bạn">
            </div>

            <div class="form-actions">
                <button type="submit" class="button button-primary btn-block">Đăng nhập</button>
            </div>
        </form>

        <div class="auth-links">
            Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký tài khoản mới</a>
        </div>
    </div>
</div>
</body>
</html>

