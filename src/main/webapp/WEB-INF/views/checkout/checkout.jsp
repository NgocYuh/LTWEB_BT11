<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Thanh toán đơn hàng (COD) | HNHBOOKSTORE</title>
</head>
<body>
<div class="breadcrumb">
    <a href="${pageContext.request.contextPath}/home">Trang chủ</a> &rsaquo;
    <a href="${pageContext.request.contextPath}/cart">Giỏ hàng</a> &rsaquo;
    <span>Thanh toán COD</span>
</div>

<div class="section-heading">
    <div>
        <p class="eyebrow">HNHBOOKSTORE / ĐẶT HÀNG</p>
        <h1>Thanh toán đơn hàng (COD)</h1>
    </div>
</div>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-error">
        ✕ <c:out value="${errorMessage}" />
    </div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/checkout/place-order">
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">

    <div class="checkout-layout">
        <!-- Cột trái: Thông tin giao hàng & Phương thức thanh toán -->
        <div>
            <!-- Khối 1: Thông tin người nhận -->
            <div class="panel checkout-card">
                <h3 style="margin-bottom: 20px; border-bottom: 1px solid var(--line); padding-bottom: 12px;">
                    1. Thông tin giao nhận hàng
                </h3>

                <div class="form-group">
                    <label class="form-label" for="customerName">Họ và tên người nhận <span class="text-danger">*</span></label>
                    <input type="text"
                           id="customerName"
                           name="customerName"
                           class="form-input"
                           required
                           maxlength="100"
                           placeholder="Ví dụ: Nguyễn Văn A"
                           value="<c:out value='${customerName}' />">
                </div>

                <div class="form-group">
                    <label class="form-label" for="phone">Số điện thoại nhận hàng <span class="text-danger">*</span></label>
                    <input type="tel"
                           id="phone"
                           name="phone"
                           class="form-input"
                           required
                           pattern="^(0|\+84)[0-9]{9,10}$"
                           placeholder="Ví dụ: 0912345678"
                           value="<c:out value='${phone}' />">
                    <span class="form-help">Số điện thoại di động Việt Nam (10 chữ số). Nhân viên giao hàng sẽ liên hệ qua số này.</span>
                </div>

                <div class="form-group">
                    <label class="form-label" for="shippingAddress">Địa chỉ nhận hàng chi tiết <span class="text-danger">*</span></label>
                    <textarea id="shippingAddress"
                              name="shippingAddress"
                              class="form-input"
                              rows="3"
                              required
                              maxlength="255"
                              placeholder="Số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố..."><c:out value="${shippingAddress}" /></textarea>
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label class="form-label" for="note">Ghi chú giao hàng (không bắt buộc)</label>
                    <textarea id="note"
                              name="note"
                              class="form-input"
                              rows="2"
                              maxlength="500"
                              placeholder="Ví dụ: Giao giờ hành chính, gọi điện trước khi đến..."><c:out value="${note}" /></textarea>
                </div>
            </div>

            <!-- Khối 2: Phương thức thanh toán -->
            <div class="panel checkout-card">
                <h3 style="margin-bottom: 20px; border-bottom: 1px solid var(--line); padding-bottom: 12px;">
                    2. Phương thức thanh toán
                </h3>

                <div class="checkout-method-box">
                    <input type="radio" id="codMethod" name="paymentMethod" value="COD" checked style="margin-top: 3px;">
                    <div>
                        <label for="codMethod" style="font-weight: 700; font-size: 15px; cursor: pointer;">
                            💵 Thanh toán tiền mặt khi nhận hàng (COD - Cash On Delivery)
                        </label>
                        <p style="margin: 6px 0 0; font-size: 13px; color: var(--muted); line-height: 1.5;">
                            Quý khách sẽ thanh toán toàn bộ số tiền đơn hàng cho shipper ngay khi nhận và đồng kiểm tra kiện sách. An toàn, tiện lợi và không cần thẻ thanh toán quốc tế.
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Cột phải: Xem trước kiện hàng & Xác nhận đặt hàng -->
        <div class="cart-summary-wrapper">
            <div class="panel cart-summary-card">
                <h3 style="margin-bottom: 18px; border-bottom: 1px solid var(--line); padding-bottom: 12px;">
                    Đơn hàng (${sessionScope.cart.totalQuantity} cuốn)
                </h3>

                <!-- Danh sách sản phẩm thu nhỏ -->
                <div style="max-height: 280px; overflow-y: auto; margin-bottom: 18px; padding-right: 4px;">
                    <c:forEach var="item" items="${sessionScope.cart.items}">
                        <div class="checkout-item-mini">
                            <img src="${pageContext.request.contextPath}/assets/images/covers/${item.book.coverImage}"
                                 alt="${item.book.title}"
                                 class="checkout-thumb"
                                 onerror="this.src='${pageContext.request.contextPath}/assets/images/book-placeholder.svg'">
                            <div class="checkout-item-info">
                                <div class="checkout-item-title" title="${item.book.title}">
                                    <c:out value="${item.book.title}" />
                                </div>
                                <div class="checkout-item-qty">
                                    ${item.quantity} x <fmt:formatNumber value="${item.book.price}" pattern="#,##0.00" /> đ
                                </div>
                            </div>
                            <div style="font-size: 13px; font-weight: 700; color: var(--slate); white-space: nowrap;">
                                <fmt:formatNumber value="${item.totalPrice}" pattern="#,##0.00" /> đ
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div class="summary-row">
                    <span>Tạm tính hàng:</span>
                    <strong><fmt:formatNumber value="${sessionScope.cart.totalAmount}" pattern="#,##0.00" /> VNĐ</strong>
                </div>

                <div class="summary-row">
                    <span>Phí vận chuyển COD:</span>
                    <span style="color: #16A34A; font-weight: 700;">Miễn phí</span>
                </div>

                <div class="summary-row" style="border-top: 1px dashed var(--line); padding-top: 16px; margin-top: 16px;">
                    <span style="font-size: 16px; font-weight: 700;">Tổng thanh toán COD:</span>
                    <span class="price-tag" style="font-size: 22px;">
                        <fmt:formatNumber value="${sessionScope.cart.totalAmount}" pattern="#,##0.00" /> VNĐ
                    </span>
                </div>

                <div style="margin-top: 24px;">
                    <button type="submit" class="button button-primary btn-block" style="font-size: 16px; padding: 14px;">
                        ✓ Xác nhận đặt hàng (COD)
                    </button>
                </div>

                <div style="margin-top: 14px; text-align: center;">
                    <a href="${pageContext.request.contextPath}/cart" class="muted" style="font-size: 13px;">
                        &larr; Quay lại chỉnh sửa giỏ hàng
                    </a>
                </div>
            </div>
        </div>
    </div>
</form>

</body>
</html>
