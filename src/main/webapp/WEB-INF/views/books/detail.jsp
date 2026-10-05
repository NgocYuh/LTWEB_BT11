<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title><c:out value="${book.title}"/> | HNHBOOKSTORE</title>
</head>
<body>
<div class="detail-container">
    <nav class="breadcrumb" aria-label="Đường dẫn điều hướng">
        <a href="${pageContext.request.contextPath}/home">Trang chủ</a> &rsaquo;
        <a href="${pageContext.request.contextPath}/home#catalog">Thư viện</a> &rsaquo;
        <span><c:out value="${book.title}"/></span>
    </nav>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success" role="alert"><c:out value="${successMessage}"/></div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error" role="alert"><c:out value="${errorMessage}"/></div>
    </c:if>

    <!-- Khối trên: Bố cục ảnh bìa bên trái, thông tin chi tiết bên phải -->
    <section class="book-detail-main">
        <div class="detail-cover-wrapper">
            <img src="${pageContext.request.contextPath}/${book.coverImage}"
                 alt="<c:out value='${book.title}'/>"
                 class="detail-cover-img"
                 loading="lazy">
        </div>

        <div class="detail-info">
            <p class="eyebrow">THÔNG TIN XUẤT BẢN</p>
            <h1 class="detail-title"><c:out value="${book.title}"/></h1>

            <div class="detail-attributes">
                <div class="attr-row">
                    <span class="attr-label">Mã isbn:</span>
                    <span class="attr-value"><c:out value="${book.isbn}"/></span>
                </div>
                <div class="attr-row">
                    <span class="attr-label">Tác giả:</span>
                    <span class="attr-value"><strong><c:out value="${book.authorNames}"/></strong></span>
                </div>
                <div class="attr-row">
                    <span class="attr-label">Publisher:</span>
                    <span class="attr-value"><c:out value="${book.publisher}"/></span>
                </div>
                <div class="attr-row">
                    <span class="attr-label">Publisher_date:</span>
                    <span class="attr-value"><c:out value="${book.publishDate}"/></span>
                </div>
                <div class="attr-row">
                    <span class="attr-label">Quantity:</span>
                    <span class="attr-value"><c:out value="${book.quantity}"/> cuốn</span>
                </div>
                <c:if test="${not empty book.price}">
                    <div class="attr-row">
                        <span class="attr-label">Giá bán:</span>
                        <span class="attr-value price-tag"><c:out value="${book.price}"/> đ</span>
                    </div>
                </c:if>
                <div class="attr-row">
                    <span class="attr-label">Reviews:</span>
                    <span class="attr-value">
                        <span class="reviews-badge">Reviews (<c:out value="${book.reviewsCount}"/>)</span>
                    </span>
                </div>
            </div>

            <c:if test="${not empty book.description}">
                <div class="detail-description">
                    <h3>Giới thiệu nội dung</h3>
                    <p><c:out value="${book.description}"/></p>
                </div>
            </c:if>
        </div>
    </section>

    <!-- Khối dưới: Danh sách đánh giá & Form nhận xét -->
    <section class="detail-reviews-section">
        <div class="section-heading">
            <h2>Reviews</h2>
            <span class="muted"><c:out value="${book.reviewsCount}"/> nhận xét từ độc giả</span>
        </div>

        <div class="reviews-list">
            <c:choose>
                <c:when test="${not empty reviews}">
                    <c:forEach items="${reviews}" var="rev">
                        <article class="review-item">
                            <div class="review-header">
                                <span class="review-user-name">[<c:out value="${rev.userName}"/>]</span>:
                                <span class="review-text"><c:out value="${rev.reviewText}"/></span>
                            </div>
                            <c:if test="${not empty rev.rating}">
                                <div class="review-rating-tag">★ <c:out value="${rev.rating}"/>/10</div>
                            </c:if>
                        </article>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p class="empty-state">Chưa có đánh giá nào cho cuốn sách này. Hãy là người đầu tiên chia sẻ cảm nhận của bạn!</p>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Form gửi đánh giá -->
        <div class="review-form-card">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <h3>
                        <c:choose>
                            <c:when test="${not empty myReview}">Cập nhật đánh giá của bạn</c:when>
                            <c:otherwise>Viết nhận xét của bạn</c:otherwise>
                        </c:choose>
                    </h3>
                    <form method="post" action="${pageContext.request.contextPath}/books/detail">
                        <input type="hidden" name="csrf" value="<c:out value='${sessionScope.csrf}'/>">
                        <input type="hidden" name="bookId" value="<c:out value='${book.bookid}'/>">

                        <div class="form-group">
                            <label class="form-label" for="rating">Điểm đánh giá (1 — 10 điểm)</label>
                            <select class="form-input" id="rating" name="rating" style="max-width:200px;">
                                <option value="">-- Chọn điểm số --</option>
                                <c:forEach begin="1" end="10" var="i">
                                    <option value="${i}" <c:if test="${myReview.rating == i}">selected</c:if>>
                                        ${i} điểm <c:if test="${i == 10}">(Xuất sắc)</c:if>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="reviewText">Nội dung nhận xét <span style="color:var(--danger)">*</span></label>
                            <textarea class="form-input" id="reviewText" name="reviewText" rows="4" required
                                      placeholder="Chia sẻ cảm nghĩ, góc nhìn của bạn về cuốn sách này..."><c:out value="${myReview.reviewText}"/></textarea>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="button button-primary">
                                <c:choose>
                                    <c:when test="${not empty myReview}">Cập nhật đánh giá</c:when>
                                    <c:otherwise>Gửi đánh giá</c:otherwise>
                                </c:choose>
                            </button>
                        </div>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="panel" style="text-align:center;">
                        <p class="muted">Vui lòng đăng nhập để gửi đánh giá và chia sẻ cảm nghĩ của bạn.</p>
                        <a class="button button-primary"
                           href="${pageContext.request.contextPath}/login?redirect=${pageContext.request.contextPath}/books/detail?bookId=${book.bookid}">
                            Đăng nhập để nhận xét
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</div>
</body>
</html>

