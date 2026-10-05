<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đặt hàng thành công #${order.orderId} | HNHBOOKSTORE</title>
</head>
<body>
<div class="breadcrumb">
    <a href="${pageContext.request.contextPath}/home">Trang chủ</a> &rsaquo;
    <span>Đặt hàng thành công</span>
</div>

<div class="checkout-success-card panel">
    <div class="success-icon-large">🎉</div>
    <p class="eyebrow" style="color: #16A34A; margin-bottom: 8px;">ĐẶT HÀNG THÀNH CÔNG</p>
    <h1 style="font-size: 2.2rem; margin-bottom: 12px;">Cảm ơn bạn đã mua sách!</h1>
    <p class="muted" style="max-width: 580px; margin: 0 auto 24px; font-size: 15px;">
        Đơn hàng <strong>#${order.orderId}</strong> của bạn đã được tiếp nhận thành công. Nhân viên HNHBOOKSTORE sẽ liên hệ sớm nhất để xác nhận và giao sách đến tận tay bạn.
    </p>

    <!-- Hộp thông tin chi tiết đơn hàng -->
    <div class="order-details-box">
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; border-bottom: 1px solid var(--line); padding-bottom: 20px; margin-bottom: 20px;">
            <div>
                <span class="muted" style="font-size: 12px; text-transform: uppercase; font-weight: 700; letter-spacing: .5px;">Mã đơn hàng</span>
                <div style="font-size: 18px; font-weight: 800; color: var(--blue); margin-top: 4px;">#${order.orderId}</div>
            </div>
            <div>
                <span class="muted" style="font-size: 12px; text-transform: uppercase; font-weight: 700; letter-spacing: .5px;">Phương thức thanh toán</span>
                <div style="font-size: 14px; font-weight: 700; color: var(--slate); margin-top: 4px;">
                    💵 Tiền mặt khi nhận hàng (COD)
                </div>
            </div>
            <div>
                <span class="muted" style="font-size: 12px; text-transform: uppercase; font-weight: 700; letter-spacing: .5px;">Trạng thái đơn hàng</span>
                <div style="margin-top: 4px;">
                    <span class="badge-author" style="background: #FEF3C7; color: #B45309; border-color: #FDE68A;">
                        ${order.status} - Chờ xử lý
                    </span>
                </div>
            </div>
            <div>
                <span class="muted" style="font-size: 12px; text-transform: uppercase; font-weight: 700; letter-spacing: .5px;">Tổng tiền cần thanh toán</span>
                <div class="price-tag" style="font-size: 18px; margin-top: 4px;">
                    <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00" /> VNĐ
                </div>
            </div>
        </div>

        <!-- Thông tin người nhận -->
        <div style="margin-bottom: 24px;">
            <h4 style="font-size: 14px; margin-bottom: 10px; color: var(--ink);">Địa chỉ & Người nhận hàng:</h4>
            <div style="font-size: 14px; line-height: 1.7; color: var(--slate);">
                <div>Người nhận: <strong><c:out value="${order.customerName}" /></strong></div>
                <div>Điện thoại: <strong><c:out value="${order.phone}" /></strong></div>
                <div>Địa chỉ: <strong><c:out value="${order.shippingAddress}" /></strong></div>
                <c:if test="${not empty order.note}">
                    <div>Ghi chú: <em><c:out value="${order.note}" /></em></div>
                </c:if>
            </div>
        </div>

        <!-- Bảng danh sách sản phẩm đã đặt -->
        <div>
            <h4 style="font-size: 14px; margin-bottom: 12px; color: var(--ink);">Danh sách sách đã đặt:</h4>
            <table class="data-table" style="background: white; border-radius: 8px; overflow: hidden; border: 1px solid var(--line);">
                <thead>
                    <tr>
                        <th>Tên sách</th>
                        <th style="text-align: right;">Đơn giá</th>
                        <th style="text-align: center;">Số lượng</th>
                        <th style="text-align: right;">Thành tiền</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="detail" items="${order.items}">
                        <tr>
                            <td>
                                <strong><c:out value="${detail.bookTitle}" /></strong>
                            </td>
                            <td style="text-align: right;">
                                <fmt:formatNumber value="${detail.unitPrice}" pattern="#,##0.00" /> đ
                            </td>
                            <td style="text-align: center;">
                                ${detail.quantity}
                            </td>
                            <td style="text-align: right; font-weight: 700; color: var(--orange);">
                                <fmt:formatNumber value="${detail.subtotal}" pattern="#,##0.00" /> đ
                            </td>
                        </tr>
                    </c:forEach>
                    <tr style="background: #F8FAFC; font-weight: 700;">
                        <td colspan="3" style="text-align: right; font-size: 15px;">Tổng cộng thanh toán (COD):</td>
                        <td style="text-align: right; font-size: 16px; color: var(--orange);">
                            <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00" /> VNĐ
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Khuyến cáo COD -->
    <div style="background: #EFF6FF; border: 1px solid #BFDBFE; border-radius: 8px; padding: 14px 18px; margin-top: 24px; text-align: left; font-size: 13px; color: #1E40AF; line-height: 1.5;">
        💡 <strong>Lưu ý nhận hàng COD:</strong> Quý khách vui lòng chú ý điện thoại từ nhân viên giao hàng và chuẩn bị sẵn số tiền mặt chính xác khi nhận bưu phẩm. Quý khách hoàn toàn được quyền mở hộp đồng kiểm trước khi thanh toán tiền mặt.
    </div>

    <div style="margin-top: 32px; display: flex; justify-content: center; gap: 16px;">
        <a href="${pageContext.request.contextPath}/home" class="button button-primary" style="padding-inline: 28px;">
            &larr; Tiếp tục mua sắm
        </a>
    </div>
</div>

</body>
</html>
