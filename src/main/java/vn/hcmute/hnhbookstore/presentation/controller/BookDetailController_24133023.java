package vn.hcmute.hnhbookstore.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.hcmute.hnhbookstore.data.BookDao_24133023;
import vn.hcmute.hnhbookstore.data.RatingDao_24133023;
import vn.hcmute.hnhbookstore.model.Book_24133023;
import vn.hcmute.hnhbookstore.model.Rating_24133023;
import vn.hcmute.hnhbookstore.model.User_24133023;
import vn.hcmute.hnhbookstore.util.CsrfUtil_24133023;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = {"/books/detail"})
public final class BookDetailController_24133023 extends HttpServlet {
    private final BookDao_24133023 bookDao = new BookDao_24133023();
    private final RatingDao_24133023 ratingDao = new RatingDao_24133023();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bookIdParam = request.getParameter("bookId");
        if (bookIdParam == null || bookIdParam.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số bookId.");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(bookIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã sách không hợp lệ.");
            return;
        }

        try {
            Book_24133023 book = bookDao.findById(bookId);
            if (book == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy cuốn sách yêu cầu.");
                return;
            }

            List<Rating_24133023> reviews = ratingDao.findRatingsByBookId(bookId);

            HttpSession session = request.getSession(true);
            CsrfUtil_24133023.getOrCreateToken(session);
            User_24133023 currentUser = (User_24133023) session.getAttribute("currentUser");
            if (currentUser != null) {
                Rating_24133023 myReview = ratingDao.findUserRating(currentUser.getId(), bookId);
                request.setAttribute("myReview", myReview);
            }

            if ("1".equals(request.getParameter("saved"))) {
                request.setAttribute("successMessage", "Cảm ơn bạn! Đánh giá đã được lưu thành công.");
            }

            request.setAttribute("book", book);
            request.setAttribute("reviews", reviews);
            request.getRequestDispatcher("/WEB-INF/views/books/detail.jsp").forward(request, response);
        } catch (SQLException e) {
            getServletContext().log("Error loading book detail: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi máy chủ khi tải chi tiết sách.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User_24133023 currentUser = (session != null)
                ? (User_24133023) session.getAttribute("currentUser")
                : null;

        String bookIdParam = request.getParameter("bookId");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?required=1");
            return;
        }

        if (!CsrfUtil_24133023.validateToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Yêu cầu không hợp lệ hoặc phiên bảo mật đã hết hạn.");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(bookIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã sách không hợp lệ.");
            return;
        }

        String scoreParam = request.getParameter("rating");
        Integer ratingScore = null;
        if (scoreParam != null && !scoreParam.isBlank()) {
            try {
                int score = Integer.parseInt(scoreParam);
                if (score >= 1 && score <= 10) {
                    ratingScore = score;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String reviewText = request.getParameter("reviewText");
        if (reviewText == null || reviewText.trim().isEmpty()) {
            // Re-render page with error message
            try {
                Book_24133023 book = bookDao.findById(bookId);
                List<Rating_24133023> reviews = ratingDao.findRatingsByBookId(bookId);
                Rating_24133023 myReview = ratingDao.findUserRating(currentUser.getId(), bookId);
                request.setAttribute("book", book);
                request.setAttribute("reviews", reviews);
                request.setAttribute("myReview", myReview);
                request.setAttribute("errorMessage", "Nội dung nhận xét không được để trống.");
                request.getRequestDispatcher("/WEB-INF/views/books/detail.jsp").forward(request, response);
                return;
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi kết nối CSDL.");
                return;
            }
        }

        try {
            ratingDao.upsertRating(currentUser.getId(), bookId, ratingScore, reviewText.trim());
            // POST-Redirect-GET pattern
            response.sendRedirect(request.getContextPath() + "/books/detail?bookId=" + bookId + "&saved=1");
        } catch (SQLException e) {
            getServletContext().log("Error saving review: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi lưu đánh giá.");
        }
    }
}

