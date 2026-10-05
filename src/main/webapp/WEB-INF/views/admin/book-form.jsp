<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title><c:out value="${formTitle}" /> | HNHBOOKSTORE</title>
</head>
<body>
<div class="breadcrumb">
    <a href="${pageContext.request.contextPath}/admin/books">Quản lý kho sách</a> &rsaquo;
    <span><c:out value="${formTitle}" /></span>
</div>

<div class="admin-header-row">
    <div>
        <p class="eyebrow">HNHBOOKSTORE / QUẢN TRỊ</p>
        <h1><c:out value="${formTitle}" /></h1>
        <p class="muted">Nhập đầy đủ thông tin sách theo đúng schema dữ liệu gốc của hệ thống.</p>
    </div>
</div>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-error">
        <strong>✕ Có lỗi xảy ra:</strong> <c:out value="${errorMessage}" />
    </div>
</c:if>

<c:if test="${mode == 'edit' && fn:length(book.authors) > 1}">
    <div class="alert alert-info">
        ℹ Cuốn sách này hiện có <strong>${fn:length(book.authors)}</strong> tác giả liên kết (${book.authorNames}). Khi bạn lưu form này, sách sẽ được gán lại với tác giả được chọn bên dưới theo đúng quy định phân hệ quản trị.
    </div>
</c:if>

<div class="panel form-card">
    <form method="post" action="${pageContext.request.contextPath}/admin/books/${mode == 'edit' ? 'edit' : 'new'}" novalidate>
        <input type="hidden" name="csrf" value="${csrf}">
        <c:if test="${mode == 'edit'}">
            <input type="hidden" name="bookId" value="${book.bookid}">
        </c:if>

        <!-- 1. Tiêu đề sách -->
        <div class="form-group">
            <label for="title" class="form-label">Tiêu đề sách <span class="text-danger">*</span></label>
            <input type="text"
                   id="title"
                   name="title"
                   class="form-input"
                   maxlength="200"
                   required
                   placeholder="Nhập tên cuốn sách..."
                   value="<c:out value="${not empty paramTitle ? paramTitle : (book != null ? book.title : '')}" />">
            <small class="form-help">Bắt buộc, tối đa 200 ký tự (chuẩn VARCHAR(200)).</small>
        </div>

        <div class="form-grid-2">
            <!-- 2. Tác giả -->
            <div class="form-group">
                <label for="authorId" class="form-label">Tác giả <span class="text-danger">*</span></label>
                <c:choose>
                    <c:when test="${empty authors}">
                        <div class="alert alert-error" style="margin: 0; padding: 10px;">
                            ⚠ Chưa có dữ liệu tác giả trong hệ thống. Vui lòng nạp seed tác giả trước.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:set var="curAId" value="${not empty selectedAuthorId ? selectedAuthorId : (book != null && not empty book.authors ? book.authors[0].id : '')}" />
                        <select id="authorId" name="authorId" class="form-input" required>
                            <option value="">-- Chọn một tác giả --</option>
                            <c:forEach items="${authors}" var="a">
                                <option value="${a.id}" ${curAId == a.id ? 'selected' : ''}>
                                    <c:out value="${a.name}" /> <c:if test="${a.dateOfBirth != null}">(sinh: ${a.dateOfBirth})</c:if>
                                </option>
                            </c:forEach>
                        </select>
                        <small class="form-help">Quan hệ lưu tại bảng trung gian <code>book_author</code>.</small>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- 3. Mã ISBN -->
            <div class="form-group">
                <label for="isbn" class="form-label">Mã ISBN</label>
                <input type="number"
                       id="isbn"
                       name="isbn"
                       class="form-input"
                       placeholder="Ví dụ: 241330201"
                       value="<c:out value="${not empty paramIsbn ? paramIsbn : (book != null && book.isbn != null ? book.isbn : '')}" />">
                <small class="form-help">Số nguyên INT chuẩn CSDL (tối đa 2.147.483.647).</small>
            </div>
        </div>

        <div class="form-grid-2">
            <!-- 4. Nhà xuất bản -->
            <div class="form-group">
                <label for="publisher" class="form-label">Nhà xuất bản</label>
                <input type="text"
                       id="publisher"
                       name="publisher"
                       class="form-input"
                       maxlength="100"
                       placeholder="Ví dụ: NXB Trẻ, NXB Kim Đồng..."
                       value="<c:out value="${not empty paramPublisher ? paramPublisher : (book != null ? book.publisher : '')}" />">
                <small class="form-help">Tối đa 100 ký tự (chuẩn VARCHAR(100)).</small>
            </div>

            <!-- 5. Giá bán -->
            <div class="form-group">
                <label for="price" class="form-label">Giá bán (VNĐ)</label>
                <input type="number"
                       id="price"
                       name="price"
                       class="form-input"
                       step="0.01"
                       min="0"
                       max="9999.99"
                       placeholder="120.00"
                       value="<c:out value="${not empty paramPrice ? paramPrice : (book != null && book.price != null ? book.price : '')}" />">
                <small class="form-help">Định dạng DECIMAL(6,2), từ 0.00 đến 9999.99.</small>
            </div>
        </div>

        <div class="form-grid-2">
            <!-- 6. Ngày xuất bản -->
            <div class="form-group">
                <label for="publishDate" class="form-label">Ngày xuất bản</label>
                <input type="date"
                       id="publishDate"
                       name="publishDate"
                       class="form-input"
                       value="<c:out value="${not empty paramPublishDate ? paramPublishDate : (book != null && book.publishDate != null ? book.publishDate : '')}" />">
                <small class="form-help">Định dạng ngày chuẩn YYYY-MM-DD.</small>
            </div>

            <!-- 7. Số lượng tồn kho -->
            <div class="form-group">
                <label for="quantity" class="form-label">Số lượng tồn kho</label>
                <input type="number"
                       id="quantity"
                       name="quantity"
                       class="form-input"
                       min="0"
                       placeholder="0"
                       value="<c:out value="${not empty paramQuantity ? paramQuantity : (book != null && book.quantity != null ? book.quantity : '0')}" />">
                <small class="form-help">Số nguyên không âm (INT).</small>
            </div>
        </div>

        <!-- 8. Ảnh bìa -->
        <div class="form-group">
            <label for="coverImage" class="form-label">Đường dẫn ảnh bìa</label>
            <input type="text"
                   id="coverImage"
                   name="coverImage"
                   class="form-input"
                   maxlength="100"
                   placeholder="assets/images/book-placeholder.svg"
                   value="<c:out value="${not empty paramCoverImage ? paramCoverImage : (book != null && not empty book.coverImage ? book.coverImage : 'assets/images/book-placeholder.svg')}" />">
            <small class="form-help">Tối đa 100 ký tự (VARCHAR(100)). Mặc định: <code>assets/images/book-placeholder.svg</code>.</small>
        </div>

        <!-- 9. Mô tả tóm tắt -->
        <div class="form-group">
            <label for="description" class="form-label">Mô tả nội dung sách</label>
            <textarea id="description"
                      name="description"
                      class="form-input"
                      rows="5"
                      placeholder="Nhập giới thiệu hoặc tóm tắt nội dung cuốn sách..."><c:out value="${not empty paramDescription ? paramDescription : (book != null ? book.description : '')}" /></textarea>
            <small class="form-help">Kiểu dữ liệu TEXT trong SQL Server.</small>
        </div>

        <div class="form-actions-bar">
            <button type="submit" class="button button-primary">
                <c:choose>
                    <c:when test="${mode == 'edit'}">✓ Cập nhật thay đổi</c:when>
                    <c:otherwise>+ Lưu cuốn sách</c:otherwise>
                </c:choose>
            </button>
            <a href="${pageContext.request.contextPath}/admin/books" class="button button-outline">
                Hủy bỏ &amp; Quay lại
            </a>
        </div>
    </form>
</div>
</body>
</html>

