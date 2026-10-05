<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Quản lý kho sách</title>
</head>
<body>
<div class="admin-header-row">
    <div>
        <p class="eyebrow">HNHBOOKSTORE / QUẢN TRỊ</p>
        <h1>Quản lý kho sách</h1>
        <p class="muted">Danh sách toàn bộ các đầu sách trong hệ thống. Hỗ trợ xem, thêm mới, điều chỉnh và xóa.</p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/admin/books/new" class="button button-primary">
            + Thêm sách mới
        </a>
    </div>
</div>

<c:if test="${param.msg == 'created'}">
    <div class="alert alert-success">✓ Đã thêm sách mới thành công vào cơ sở dữ liệu!</div>
</c:if>
<c:if test="${param.msg == 'updated'}">
    <div class="alert alert-success">✓ Cập nhật thông tin sách và tác giả thành công!</div>
</c:if>
<c:if test="${param.msg == 'deleted'}">
    <div class="alert alert-success">✓ Đã xóa sách và các đánh giá liên quan thành công (Transaction an toàn)!</div>
</c:if>

<c:if test="${param.error == 'not_found'}">
    <div class="alert alert-error">✕ Không tìm thấy cuốn sách được yêu cầu trong kho.</div>
</c:if>
<c:if test="${param.error == 'invalid_id'}">
    <div class="alert alert-error">✕ Mã định danh sách (ID) không hợp lệ.</div>
</c:if>
<c:if test="${param.error == 'delete_failed'}">
    <div class="alert alert-error">✕ Xóa sách thất bại do lỗi giao dịch hoặc ràng buộc hệ thống.</div>
</c:if>

<div class="panel table-responsive-container">
    <c:choose>
        <c:when test="${empty books}">
            <div class="empty-state">
                <p>Kho sách hiện đang trống.</p>
                <a href="${pageContext.request.contextPath}/admin/books/new" class="button button-primary">Thêm sách đầu tiên</a>
            </div>
        </c:when>
        <c:otherwise>
            <table class="data-table">
                <thead>
                    <tr>
                        <th style="width: 50px;">#ID</th>
                        <th style="width: 60px;">Bìa</th>
                        <th>ISBN</th>
                        <th>Tiêu đề sách</th>
                        <th>Tác giả</th>
                        <th>Nhà xuất bản</th>
                        <th>Giá bán</th>
                        <th>Ngày XB</th>
                        <th style="text-align: center;">SL</th>
                        <th style="text-align: center;">Đánh giá</th>
                        <th style="text-align: right; width: 170px;">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${books}" var="b">
                        <tr>
                            <td><span class="muted font-mono">#${b.bookid}</span></td>
                            <td>
                                <img src="${pageContext.request.contextPath}/${b.coverImage}"
                                     alt="Bìa"
                                     class="table-thumb"
                                     onerror="this.src='${pageContext.request.contextPath}/assets/images/book-placeholder.svg'">
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.isbn != null}">
                                        <span class="font-mono">${b.isbn}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="muted">-</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <strong><c:out value="${b.title}" /></strong>
                            </td>
                            <td>
                                <span class="badge badge-author"><c:out value="${b.authorNames}" /></span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty b.publisher}">
                                        <c:out value="${b.publisher}" />
                                    </c:when>
                                    <c:otherwise>
                                        <span class="muted">-</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.price != null}">
                                        <span class="price-tag"><fmt:formatNumber value="${b.price}" pattern="#,##0.00" /> VNĐ</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="muted">-</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.publishDate != null}">
                                        <c:out value="${b.publishDate}" />
                                    </c:when>
                                    <c:otherwise>
                                        <span class="muted">-</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td style="text-align: center;">
                                <strong><c:out value="${b.quantity != null ? b.quantity : 0}" /></strong>
                            </td>
                            <td style="text-align: center;">
                                <span class="reviews-badge">Reviews (${b.reviewsCount})</span>
                            </td>
                            <td style="text-align: right;">
                                <div class="table-actions">
                                    <a href="${pageContext.request.contextPath}/books/detail?bookId=${b.bookid}"
                                       class="button button-outline btn-sm"
                                       title="Xem trang chi tiết sách công khai">Xem</a>
                                    <a href="${pageContext.request.contextPath}/admin/books/edit?bookId=${b.bookid}"
                                       class="button button-outline btn-sm"
                                       title="Chỉnh sửa thông tin">Sửa</a>
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/books/delete"
                                          class="inline-form"
                                          onsubmit="return confirm('Bạn có chắc chắn muốn xóa cuốn sách \'${fn:escapeXml(b.title)}\'?\n\nCẢNH BÁO: Cuốn sách cùng với toàn bộ đánh giá/review liên quan sẽ bị xóa vĩnh viễn trong một giao dịch.');">
                                        <input type="hidden" name="csrf" value="${csrf}">
                                        <input type="hidden" name="bookId" value="${b.bookid}">
                                        <button type="submit" class="button button-danger btn-sm" title="Xóa sách">Xóa</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <!-- Thanh phân trang 10 sách / trang -->
            <div class="pagination-bar admin-pagination">
                <span class="pagination-info">
                    Trang <strong>${currentPage}</strong> / ${totalPages} &mdash; Tổng số: <strong>${totalBooks}</strong> cuốn sách
                </span>

                <div class="pagination-controls">
                    <c:choose>
                        <c:when test="${currentPage > 1}">
                            <a href="?page=${currentPage - 1}" class="button button-outline btn-sm">Trước</a>
                        </c:when>
                        <c:otherwise>
                            <span class="button button-outline btn-sm disabled" aria-disabled="true">Trước</span>
                        </c:otherwise>
                    </c:choose>

                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:choose>
                            <c:when test="${p == currentPage}">
                                <span class="button button-primary btn-sm" aria-current="page">${p}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="?page=${p}" class="button button-outline btn-sm">${p}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:choose>
                        <c:when test="${currentPage < totalPages}">
                            <a href="?page=${currentPage + 1}" class="button button-outline btn-sm">Sau</a>
                        </c:when>
                        <c:otherwise>
                            <span class="button button-outline btn-sm disabled" aria-disabled="true">Sau</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
