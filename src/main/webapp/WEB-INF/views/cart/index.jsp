<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Giỏ hàng của bạn | HNHBOOKSTORE</title>
</head>
<body>
<div class="breadcrumb">
    <a href="${pageContext.request.contextPath}/home">Trang chủ</a> &rsaquo;
    <span>Giỏ hàng</span>
</div>

<div class="section-heading">
    <div>
        <p class="eyebrow">HNHBOOKSTORE / MUA SẮM</p>
        <h1>Giỏ hàng của bạn</h1>
    </div>
    <c:if test="${not empty sessionScope.cart && sessionScope.cart.totalQuantity > 0}">
        <form method="post" action="${pageContext.request.contextPath}/cart/clear" onsubmit="return confirm('Bạn có chắc chắn muốn xóa toàn bộ sách trong giỏ hàng?');">
            <button type="submit" class="button button-outline btn-sm">✕ Làm trống giỏ hàng</button>
        </form>
    </c:if>
</div>

<c:if test="${param.msg == 'added'}">
    <div class="alert alert-success">✓ Đã thêm sách vào giỏ hàng thành công!</div>
</c:if>
<c:if test="${param.msg == 'updated'}">
    <div class="alert alert-success">✓ Đã cập nhật số lượng sách trong giỏ hàng!</div>
</c:if>
<c:if test="${param.msg == 'removed'}">
    <div class="alert alert-success">✓ Đã xóa sách khỏi giỏ hàng thành công!</div>
</c:if>
<c:if test="${param.msg == 'cleared'}">
    <div class="alert alert-success">✓ Đã làm trống toàn bộ giỏ hàng!</div>
</c:if>
<c:if test="${param.error == 'out_of_stock'}">
    <div class="alert alert-error">✕ Sách này hiện đã hết hàng hoặc không đủ số lượng trong kho!</div>
</c:if>

<c:choose>
    <c:when test="${empty sessionScope.cart || sessionScope.cart.totalQuantity == 0}">
        <div class="empty-state">
            <div style="font-size: 48px; margin-bottom: 16px;">🛒</div>
            <h2>Giỏ hàng của bạn đang trống</h2>
            <p class="muted">Hãy khám phá các đầu sách hấp dẫn từ các tác giả và thêm vào giỏ nhé.</p>
            <div style="margin-top: 24px;">
                <a href="${pageContext.request.contextPath}/home" class="button button-primary">
                    Khám phá sách ngay &rarr;
                </a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="cart-layout">
            <!-- Cột trái: Danh sách sản phẩm -->
            <div class="cart-items-wrapper">
                <div class="panel" style="padding: 0; overflow-x: auto;">
                    <table class="data-table cart-table">
                        <thead>
                            <tr>
                                <th style="width: 80px;">Bìa</th>
                                <th>Tên sách &amp; Tác giả</th>
                                <th style="text-align: right; width: 120px;">Đơn giá</th>
                                <th style="text-align: center; width: 160px;">Số lượng</th>
                                <th style="text-align: right; width: 130px;">Thành tiền</th>
                                <th style="text-align: center; width: 60px;">Xóa</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${sessionScope.cart.items}" var="item">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/books/detail?bookId=${item.book.bookid}">
                                            <img src="${pageContext.request.contextPath}/${item.book.coverImage}"
                                                 alt="${fn:escapeXml(item.book.title)}"
                                                 class="table-thumb"
                                                 style="width: 50px; height: 68px;"
                                                 onerror="this.src='${pageContext.request.contextPath}/assets/images/book-placeholder.svg'">
                                        </a>
                                    </td>
                                    <td>
                                        <div style="font-size: 15px; font-weight: 700; margin-bottom: 4px;">
                                            <a href="${pageContext.request.contextPath}/books/detail?bookId=${item.book.bookid}">
                                                <c:out value="${item.book.title}" />
                                            </a>
                                        </div>
                                        <div class="muted" style="font-size: 13px;">
                                            Tác giả: <strong><c:out value="${item.book.authorNames}" /></strong>
                                        </div>
                                        <div class="muted" style="font-size: 12px; margin-top: 2px;">
                                            Tồn kho: ${item.book.quantity} cuốn
                                        </div>
                                    </td>
                                    <td style="text-align: right;">
                                        <span class="price-tag">
                                            <fmt:formatNumber value="${item.book.price}" pattern="#,##0.00" /> đ
                                        </span>
                                    </td>
                                    <td style="text-align: center;">
                                        <form method="post" action="${pageContext.request.contextPath}/cart/update" class="qty-form">
                                            <input type="hidden" name="bookId" value="${item.book.bookid}">
                                            <div class="qty-control">
                                                <button type="submit" name="quantity" value="${item.quantity - 1}" class="qty-btn" title="Giảm">-</button>
                                                <input type="number"
                                                       name="quantity"
                                                       value="${item.quantity}"
                                                       min="1"
                                                       max="${item.book.quantity}"
                                                       class="qty-input"
                                                       onchange="this.form.submit()">
                                                <button type="submit"
                                                        name="quantity"
                                                        value="${item.quantity + 1}"
                                                        class="qty-btn"
                                                        ${item.quantity >= item.book.quantity ? 'disabled' : ''}
                                                        title="Tăng">+</button>
                                            </div>
                                        </form>
                                    </td>
                                    <td style="text-align: right;">
                                        <strong style="color: var(--orange); font-size: 15px;">
                                            <fmt:formatNumber value="${item.totalPrice}" pattern="#,##0.00" /> đ
                                        </strong>
                                    </td>
                                    <td style="text-align: center;">
                                        <form method="post" action="${pageContext.request.contextPath}/cart/remove" onsubmit="return confirm('Xóa cuốn sách này khỏi giỏ?');">
                                            <input type="hidden" name="bookId" value="${item.book.bookid}">
                                            <button type="submit" class="button button-danger btn-sm" style="padding: 4px 8px; min-height: 28px;" title="Xóa cuốn sách này">✕</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div style="margin-top: 18px;">
                    <a href="${pageContext.request.contextPath}/home" class="button button-outline">
                        &larr; Tiếp tục chọn sách
                    </a>
                </div>
            </div>

            <!-- Cột phải: Hộp tổng quan & Đặt hàng -->
            <div class="cart-summary-wrapper">
                <div class="panel cart-summary-card">
                    <h3 style="margin-bottom: 18px; border-bottom: 1px solid var(--line); padding-bottom: 12px;">Tóm tắt đơn hàng</h3>
                    
                    <div class="summary-row">
                        <span>Tổng số lượng sách:</span>
                        <strong>${sessionScope.cart.totalQuantity} cuốn</strong>
                    </div>

                    <div class="summary-row">
                        <span>Hình thức vận chuyển:</span>
                        <span style="color: #16A34A; font-weight: 600;">Giao hàng tiêu chuẩn</span>
                    </div>

                    <div class="summary-row" style="border-top: 1px dashed var(--line); padding-top: 16px; margin-top: 16px;">
                        <span style="font-size: 16px; font-weight: 700;">Tổng thanh toán:</span>
                        <span class="price-tag" style="font-size: 22px;">
                            <fmt:formatNumber value="${sessionScope.cart.totalAmount}" pattern="#,##0.00" /> VNĐ
                        </span>
                    </div>

                    <div style="margin-top: 26px;">
                        <a href="${pageContext.request.contextPath}/checkout" class="button button-primary btn-block" style="font-size: 16px; padding: 14px;">
                            Tiến hành thanh toán (COD) &rarr;
                        </a>
                    </div>

                    <div style="margin-top: 16px; font-size: 12px; color: var(--muted); line-height: 1.5; text-align: center;">
                        🛡 Thanh toán tiền mặt trực tiếp khi nhận hàng (COD). Kiểm tra hàng trước khi thanh toán.
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

</body>
</html>
