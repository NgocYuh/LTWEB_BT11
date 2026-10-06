<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Lịch sử đặt hàng | HNHBOOKSTORE</title>
</head>
<body>
<div class="breadcrumb">
    <a href="${pageContext.request.contextPath}/home">Trang chủ</a> &rsaquo;
    <span>Lịch sử đặt hàng</span>
</div>

<div class="section-heading">
    <div>
        <p class="eyebrow">HNHBOOKSTORE / QUẢN LÝ ĐƠN HÀNG</p>
        <h1>Lịch sử đặt hàng</h1>
    </div>
    <c:if test="${sessionScope.currentUser.admin}">
        <div>
            <c:choose>
                <c:when test="${scope == 'all'}">
                    <a href="${pageContext.request.contextPath}/orders?status=${activeStatus}&scope=my" class="button button-outline btn-sm">
                        👤 Chỉ xem đơn của tôi
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/orders?status=${activeStatus}&scope=all" class="button button-outline btn-sm">
                        🌐 Xem tất cả đơn hệ thống (Admin)
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>
</div>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-error">
        ✕ <c:out value="${errorMessage}" />
    </div>
</c:if>

<!-- Thanh lọc trạng thái 8 bước -->
<div class="orders-tabs-container">
    <nav class="orders-tabs" aria-label="Lọc đơn hàng theo trạng thái">
        <a href="${pageContext.request.contextPath}/orders?status=ALL&scope=${scope}"
           class="order-tab ${activeStatus == 'ALL' ? 'active' : ''}">
            Tất cả <span class="tab-count">${statusCounts['ALL']}</span>
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=NEW&scope=${scope}"
           class="order-tab ${activeStatus == 'NEW' ? 'active' : ''}">
            Đơn hàng mới <span class="tab-count">${statusCounts['NEW']}</span>
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=CONFIRMED&scope=${scope}"
           class="order-tab ${activeStatus == 'CONFIRMED' ? 'active' : ''}">
            Đã xác nhận <span class="tab-count">${statusCounts['CONFIRMED']}</span>
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=PREPARING&scope=${scope}"
           class="order-tab ${activeStatus == 'PREPARING' ? 'active' : ''}">
            Chuẩn bị hàng <span class="tab-count">${statusCounts['PREPARING']}</span>
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=SHIPPING&scope=${scope}"
           class="order-tab ${activeStatus == 'SHIPPING' ? 'active' : ''}">
            Vận chuyển <span class="tab-count">${statusCounts['SHIPPING']}</span>
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=DELIVERING&scope=${scope}"
           class="order-tab ${activeStatus == 'DELIVERING' ? 'active' : ''}">
            Giao hàng <span class="tab-count">${statusCounts['DELIVERING']}</span>
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=DELIVERED&scope=${scope}"
           class="order-tab ${activeStatus == 'DELIVERED' ? 'active' : ''}">
            Đã giao <span class="tab-count">${statusCounts['DELIVERED']}</span>
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=CANCELLED&scope=${scope}"
           class="order-tab ${activeStatus == 'CANCELLED' ? 'active' : ''}">
            Đơn hàng hủy <span class="tab-count">${statusCounts['CANCELLED']}</span>
        </a>
        <a href="${pageContext.request.contextPath}/orders?status=RETURNED&scope=${scope}"
           class="order-tab ${activeStatus == 'RETURNED' ? 'active' : ''}">
            Đơn hàng hoàn <span class="tab-count">${statusCounts['RETURNED']}</span>
        </a>
    </nav>
</div>

<!-- Danh sách đơn hàng -->
<c:choose>
    <c:when test="${empty orders}">
        <div class="empty-state">
            <div style="font-size: 48px; margin-bottom: 14px;">📦</div>
            <h3>Không có đơn hàng nào</h3>
            <p class="muted">
                <c:choose>
                    <c:when test="${activeStatus == 'ALL'}">
                        Bạn chưa có đơn đặt hàng nào trong hệ thống.
                    </c:when>
                    <c:otherwise>
                        Hiện tại không có đơn hàng nào thuộc trạng thái này.
                    </c:otherwise>
                </c:choose>
            </p>
            <div style="margin-top: 20px; display: flex; justify-content: center; gap: 12px;">
                <c:if test="${activeStatus != 'ALL'}">
                    <a href="${pageContext.request.contextPath}/orders?status=ALL&scope=${scope}" class="button button-outline">
                        Xem tất cả đơn hàng
                    </a>
                </c:if>
                <a href="${pageContext.request.contextPath}/home" class="button button-primary">
                    Tiếp tục mua sách
                </a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <c:forEach var="order" items="${orders}">
            <div class="order-card">
                <!-- Header của đơn hàng -->
                <div class="order-card-header">
                    <div class="order-card-header-left">
                        <span class="order-id-tag">Đơn hàng #${order.orderId}</span>
                        <span class="order-date-tag">
                            🕒 Ngày đặt: ${order.createdAt}
                        </span>
                        <c:if test="${sessionScope.currentUser.admin && scope == 'all'}">
                            <span class="badge-author" style="font-size: 11px;">Mã User: ${order.userId}</span>
                        </c:if>
                    </div>
                    <div>
                        <span class="badge-status ${order.statusBadgeClass}">
                            ● ${order.statusDisplayName}
                        </span>
                        <span class="muted" style="font-size: 11px; margin-left: 6px;" title="Giá trị lưu trong CSDL">
                            (DB: ${order.status})
                        </span>
                    </div>
                </div>

                <!-- Danh sách sách trong đơn -->
                <div class="order-card-body">
                    <c:forEach var="detail" items="${order.items}">
                        <div class="order-item-row">
                            <img src="${pageContext.request.contextPath}/assets/images/covers/${detail.coverImage}"
                                 alt="${detail.bookTitle}"
                                 class="order-item-thumb"
                                 onerror="this.src='${pageContext.request.contextPath}/assets/images/book-placeholder.svg'">
                            <div style="flex: 1; min-width: 0;">
                                <div class="order-item-title">
                                    <a href="${pageContext.request.contextPath}/books/detail?bookId=${detail.bookId}" style="color: var(--ink);">
                                        <c:out value="${detail.bookTitle}" />
                                    </a>
                                </div>
                                <div class="order-item-meta">
                                    Số lượng: <strong>x${detail.quantity}</strong> &bull; Đơn giá: <fmt:formatNumber value="${detail.unitPrice}" pattern="#,##0.00" /> đ
                                </div>
                            </div>
                            <div style="text-align: right; font-weight: 700; color: var(--slate); font-size: 14px;">
                                <fmt:formatNumber value="${detail.subtotal}" pattern="#,##0.00" /> đ
                            </div>
                        </div>
                    </c:forEach>

                    <!-- Thông tin nhận hàng rút gọn -->
                    <div style="margin-top: 14px; padding-top: 12px; border-top: 1px dashed var(--line); font-size: 13px; color: var(--muted); display: flex; justify-content: space-between; flex-wrap: wrap; gap: 8px;">
                        <div>
                            📍 Người nhận: <strong style="color: var(--ink);"><c:out value="${order.customerName}" /></strong> (<c:out value="${order.phone}" />) &mdash; Địa chỉ: <c:out value="${order.shippingAddress}" />
                        </div>
                        <c:if test="${not empty order.note}">
                            <div><em>Ghi chú: <c:out value="${order.note}" /></em></div>
                        </c:if>
                    </div>
                </div>

                <!-- Footer của đơn hàng -->
                <div class="order-card-footer">
                    <div>
                        <span class="muted" style="font-size: 13px;">Phương thức:</span>
                        <strong style="font-size: 13px; color: var(--slate);">💵 ${order.paymentMethod} (Thanh toán khi nhận hàng)</strong>
                        <span class="muted" style="font-size: 13px; margin-left: 12px;">Tổng số sách:</span>
                        <strong style="font-size: 13px;">${order.totalItemQuantity} cuốn</strong>
                    </div>
                    <div style="display: flex; align-items: center; gap: 16px;">
                        <div>
                            <span class="muted" style="font-size: 13px;">Thành tiền:</span>
                            <span class="price-tag" style="font-size: 18px; margin-left: 4px;">
                                <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00" /> VNĐ
                            </span>
                        </div>
                        <a href="${pageContext.request.contextPath}/checkout/success?orderId=${order.orderId}" class="button button-outline btn-sm">
                            Xem hóa đơn &rarr;
                        </a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<!-- Hướng dẫn kiểm thử cho sinh viên / giảng viên -->
<div style="margin-top: 36px; padding: 18px 22px; background: #F8FAFC; border: 1px solid var(--line); border-radius: 10px; font-size: 13px; color: var(--slate); line-height: 1.6;">
    <strong style="color: var(--blue);">💡 Hướng dẫn kiểm thử thay đổi trạng thái đơn hàng trong Database:</strong>
    <p style="margin: 6px 0 10px;">
        Mở SQL Server Management Studio (SSMS) hoặc lệnh <code>sqlcmd</code> và chạy lệnh cập nhật trạng thái tương ứng, sau đó tải lại trang này để kiểm tra lọc:
    </p>
    <div style="background: #1E293B; color: #F8FAFC; padding: 12px 16px; border-radius: 6px; font-family: monospace; font-size: 12.5px; overflow-x: auto;">
        -- Cập nhật đơn hàng sang các trạng thái:<br>
        UPDATE dbo.orders SET status = 'CONFIRMED' WHERE order_id = 1;   -- Đã xác nhận<br>
        UPDATE dbo.orders SET status = 'PREPARING' WHERE order_id = 2;   -- Chuẩn bị hàng<br>
        UPDATE dbo.orders SET status = 'SHIPPING' WHERE order_id = 3;    -- Vận chuyển<br>
        UPDATE dbo.orders SET status = 'DELIVERING' WHERE order_id = 4;  -- Giao hàng<br>
        UPDATE dbo.orders SET status = 'DELIVERED' WHERE order_id = 1;   -- Đã giao<br>
        UPDATE dbo.orders SET status = 'CANCELLED' WHERE order_id = 2;   -- Đơn hàng hủy<br>
        UPDATE dbo.orders SET status = 'RETURNED' WHERE order_id = 3;    -- Đơn hàng hoàn
    </div>
</div>

</body>
</html>
