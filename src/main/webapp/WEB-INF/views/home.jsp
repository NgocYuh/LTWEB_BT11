<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Trang chủ — Thư viện sách</title>
</head>
<body>
<section class="hero">
    <div>
        <p class="eyebrow">HNHBOOKSTORE / KHÔNG GIAN ĐỌC</p>
        <h1>Mở một cuốn sách.<br>Mở thêm một thế giới.</h1>
        <p class="muted">Khám phá những trang viết đặc sắc theo từng tác giả danh tiếng.</p>
        <a class="button button-primary" href="#catalog">Khám phá thư viện <span aria-hidden="true">↗</span></a>
    </div>
    <div class="hero-note">
        <span>01 / THƯ VIỆN</span>
        <p>Một khoảng lặng,<br>một câu chuyện mới.</p>
        <div class="book-lines" aria-hidden="true"></div>
    </div>
</section>

<section id="catalog">
    <c:if test="${not empty error}">
        <div class="alert alert-error" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <c:forEach items="${authorGroups}" var="group">
        <article class="author-section" id="author-${group.author.authorId}">
            <div class="section-heading">
                <h2>Tác giả: <c:out value="${group.author.authorName}"/></h2>
                <span class="muted">Tổng cộng <c:out value="${group.totalBooks}"/> cuốn sách</span>
            </div>

            <c:choose>
                <c:when test="${not empty group.books}">
                    <div class="book-grid">
                        <c:forEach items="${group.books}" var="book">
                            <article class="book-card">
                                <div class="book-card-cover">
                                    <img src="${pageContext.request.contextPath}/${book.coverImage}"
                                         alt="<c:out value='${book.title}'/>"
                                         loading="lazy">
                                </div>
                                <div class="book-card-body">
                                    <h3 class="book-card-title">
                                        <a href="${pageContext.request.contextPath}/books/detail?bookId=${book.bookid}">
                                            <c:out value="${book.title}"/>
                                        </a>
                                    </h3>
                                    <div class="book-card-meta">
                                        <p><strong>Mã ISBN:</strong> <c:out value="${book.isbn}"/></p>
                                        <p><strong>Tác giả:</strong> <c:out value="${book.authorNames}"/></p>
                                        <p><strong>Nhà xuất bản:</strong> <c:out value="${book.publisher}"/></p>
                                        <p><strong>Ngày xuất bản:</strong> <c:out value="${book.publishDate}"/></p>
                                        <p><strong>Số lượng:</strong> <c:out value="${book.quantity}"/></p>
                                        <c:if test="${not empty book.price}">
                                            <p><strong>Giá:</strong> <span class="price-tag"><c:out value="${book.price}"/> đ</span></p>
                                        </c:if>
                                    </div>
                                    <div class="book-card-footer">
                                        <span class="reviews-badge">Reviews (<c:out value="${book.reviewsCount}"/>)</span>
                                        <a class="button button-outline btn-sm"
                                           href="${pageContext.request.contextPath}/books/detail?bookId=${book.bookid}">
                                            Xem chi tiết
                                        </a>
                                    </div>
                                </div>
                            </article>
                        </c:forEach>
                    </div>

                    <c:if test="${group.totalPages > 1}">
                        <nav class="pagination-bar" aria-label="Phân trang tác giả ${group.author.authorName}">
                            <c:choose>
                                <c:when test="${group.hasPrevious()}">
                                    <a class="button button-outline btn-sm"
                                       href="${pageContext.request.contextPath}/home?authorId=${group.author.authorId}&page=${group.currentPage - 1}#author-${group.author.authorId}">
                                        Trước
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <button class="button button-outline btn-sm" disabled>Trước</button>
                                </c:otherwise>
                            </c:choose>

                            <span class="pagination-info">Trang <strong><c:out value="${group.currentPage}"/></strong> / <c:out value="${group.totalPages}"/></span>

                            <c:choose>
                                <c:when test="${group.hasNext()}">
                                    <a class="button button-outline btn-sm"
                                       href="${pageContext.request.contextPath}/home?authorId=${group.author.authorId}&page=${group.currentPage + 1}#author-${group.author.authorId}">
                                        Sau
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <button class="button button-outline btn-sm" disabled>Sau</button>
                                </c:otherwise>
                            </c:choose>
                        </nav>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <p class="empty-state">Tác giả này hiện chưa có sách trong thư viện.</p>
                </c:otherwise>
            </c:choose>
        </article>
    </c:forEach>

    <c:if test="${empty authorGroups and empty error}">
        <p class="empty-state">Thư viện hiện chưa có dữ liệu sách.</p>
    </c:if>
</section>
</body>
</html>
