package vn.hcmute.hnhbookstore.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.hcmute.hnhbookstore.data.AuthorDao_24133023;
import vn.hcmute.hnhbookstore.data.BookDao_24133023;
import vn.hcmute.hnhbookstore.model.Author_24133023;
import vn.hcmute.hnhbookstore.model.Book_24133023;
import vn.hcmute.hnhbookstore.util.CsrfUtil_24133023;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet(urlPatterns = {
        "/admin",
        "/admin/books",
        "/admin/books/new",
        "/admin/books/edit",
        "/admin/books/delete"
})
public final class AdminController_24133023 extends HttpServlet {
    private static final int PAGE_SIZE = 10;
    private static final BigDecimal MAX_DECIMAL_PRICE = new BigDecimal("9999.99");

    private final BookDao_24133023 bookDao = new BookDao_24133023();
    private final AuthorDao_24133023 authorDao = new AuthorDao_24133023();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        HttpSession session = request.getSession(true);
        String csrfToken = CsrfUtil_24133023.getOrCreateToken(session);
        request.setAttribute("csrf", csrfToken);

        if ("/admin/books/new".equals(path)) {
            showNewForm(request, response);
        } else if ("/admin/books/edit".equals(path)) {
            showEditForm(request, response);
        } else if ("/admin/books/delete".equals(path)) {
            // Delete must be POST
            response.sendRedirect(request.getContextPath() + "/admin/books");
        } else {
            // /admin or /admin/books
            showBookList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CsrfUtil_24133023.validateToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Mã xác thực CSRF không hợp lệ hoặc đã hết hạn.");
            return;
        }

        String path = request.getServletPath();
        if ("/admin/books/new".equals(path)) {
            processCreateBook(request, response);
        } else if ("/admin/books/edit".equals(path)) {
            processUpdateBook(request, response);
        } else if ("/admin/books/delete".equals(path)) {
            processDeleteBook(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/books");
        }
    }

    private void showBookList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isBlank()) {
            try {
                page = Integer.parseInt(pageParam.trim());
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        if (page < 1) page = 1;

        try {
            int totalBooks = bookDao.countTotalBooks();
            int totalPages = (int) Math.ceil((double) totalBooks / PAGE_SIZE);
            if (totalPages == 0) totalPages = 1;
            if (page > totalPages) page = totalPages;

            List<Book_24133023> books = bookDao.findAllBooksPaged(page, PAGE_SIZE);

            request.setAttribute("books", books);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalBooks", totalBooks);
            request.setAttribute("pageSize", PAGE_SIZE);

            request.getRequestDispatcher("/WEB-INF/views/admin/books.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Lỗi truy vấn danh sách sách quản trị: " + e.getMessage(), e);
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Author_24133023> authors = authorDao.findAll();
            request.setAttribute("authors", authors);
            request.setAttribute("mode", "create");
            request.setAttribute("formTitle", "Thêm sách mới");
            request.getRequestDispatcher("/WEB-INF/views/admin/book-form.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Lỗi tải danh mục tác giả: " + e.getMessage(), e);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bookIdParam = request.getParameter("bookId");
        if (bookIdParam == null || bookIdParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/books?error=invalid_id");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(bookIdParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/books?error=invalid_id");
            return;
        }

        try {
            Book_24133023 book = bookDao.findById(bookId);
            if (book == null) {
                response.sendRedirect(request.getContextPath() + "/admin/books?error=not_found");
                return;
            }

            List<Author_24133023> authors = authorDao.findAll();
            int selectedAuthorId = 0;
            if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                selectedAuthorId = book.getAuthors().get(0).getId();
            }

            request.setAttribute("book", book);
            request.setAttribute("authors", authors);
            request.setAttribute("selectedAuthorId", selectedAuthorId);
            request.setAttribute("mode", "edit");
            request.setAttribute("formTitle", "Chỉnh sửa sách #" + bookId);
            request.getRequestDispatcher("/WEB-INF/views/admin/book-form.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Lỗi tải thông tin sách: " + e.getMessage(), e);
        }
    }

    private void processCreateBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ParsedBookData_24133023 data = parseAndValidate(request);
        if (data.errorMessage != null) {
            try {
                request.setAttribute("errorMessage", data.errorMessage);
                request.setAttribute("authors", authorDao.findAll());
                request.setAttribute("mode", "create");
                request.setAttribute("formTitle", "Thêm sách mới");
                preserveFormInput(request);
                request.getRequestDispatcher("/WEB-INF/views/admin/book-form.jsp").forward(request, response);
                return;
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }

        try {
            Book_24133023 book = new Book_24133023(
                    0,
                    data.isbn,
                    data.title,
                    data.publisher,
                    data.price,
                    data.description,
                    data.publishDate,
                    data.coverImage,
                    data.quantity,
                    0
            );
            bookDao.insertBook(book, data.authorId);
            response.sendRedirect(request.getContextPath() + "/admin/books?msg=created");
        } catch (SQLException e) {
            try {
                request.setAttribute("errorMessage", "Không thể lưu sách: " + e.getMessage());
                request.setAttribute("authors", authorDao.findAll());
                request.setAttribute("mode", "create");
                request.setAttribute("formTitle", "Thêm sách mới");
                preserveFormInput(request);
                request.getRequestDispatcher("/WEB-INF/views/admin/book-form.jsp").forward(request, response);
            } catch (SQLException ex) {
                throw new ServletException(ex);
            }
        }
    }

    private void processUpdateBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bookIdParam = request.getParameter("bookId");
        if (bookIdParam == null || bookIdParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/books?error=invalid_id");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(bookIdParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/books?error=invalid_id");
            return;
        }

        ParsedBookData_24133023 data = parseAndValidate(request);
        if (data.errorMessage != null) {
            try {
                request.setAttribute("errorMessage", data.errorMessage);
                request.setAttribute("authors", authorDao.findAll());
                request.setAttribute("mode", "edit");
                request.setAttribute("formTitle", "Chỉnh sửa sách #" + bookId);
                preserveFormInput(request);
                request.getRequestDispatcher("/WEB-INF/views/admin/book-form.jsp").forward(request, response);
                return;
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }

        try {
            Book_24133023 book = new Book_24133023(
                    bookId,
                    data.isbn,
                    data.title,
                    data.publisher,
                    data.price,
                    data.description,
                    data.publishDate,
                    data.coverImage,
                    data.quantity,
                    0
            );
            bookDao.updateBook(book, data.authorId);
            response.sendRedirect(request.getContextPath() + "/admin/books?msg=updated");
        } catch (SQLException e) {
            try {
                request.setAttribute("errorMessage", "Không thể cập nhật sách: " + e.getMessage());
                request.setAttribute("authors", authorDao.findAll());
                request.setAttribute("mode", "edit");
                request.setAttribute("formTitle", "Chỉnh sửa sách #" + bookId);
                preserveFormInput(request);
                request.getRequestDispatcher("/WEB-INF/views/admin/book-form.jsp").forward(request, response);
            } catch (SQLException ex) {
                throw new ServletException(ex);
            }
        }
    }

    private void processDeleteBook(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bookIdParam = request.getParameter("bookId");
        if (bookIdParam == null || bookIdParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/books?error=invalid_id");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(bookIdParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/books?error=invalid_id");
            return;
        }

        try {
            bookDao.deleteBook(bookId);
            response.sendRedirect(request.getContextPath() + "/admin/books?msg=deleted");
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/admin/books?error=delete_failed");
        }
    }

    private ParsedBookData_24133023 parseAndValidate(HttpServletRequest request) {
        ParsedBookData_24133023 data = new ParsedBookData_24133023();

        // 1. Title (Required, varchar(200))
        String title = request.getParameter("title");
        if (title == null || title.trim().isBlank()) {
            data.errorMessage = "Tiêu đề sách không được để trống.";
            return data;
        }
        data.title = title.trim();
        if (data.title.length() > 200) {
            data.errorMessage = "Tiêu đề sách không được vượt quá 200 ký tự.";
            return data;
        }

        // 2. Author (Required, dropdown from author table)
        String authorIdStr = request.getParameter("authorId");
        if (authorIdStr == null || authorIdStr.trim().isBlank()) {
            data.errorMessage = "Vui lòng chọn một tác giả cho cuốn sách.";
            return data;
        }
        try {
            data.authorId = Integer.parseInt(authorIdStr.trim());
            if (data.authorId <= 0) {
                data.errorMessage = "Tác giả được chọn không hợp lệ.";
                return data;
            }
        } catch (NumberFormatException e) {
            data.errorMessage = "Mã tác giả không hợp lệ.";
            return data;
        }

        // 3. ISBN (Optional, int)
        String isbnStr = request.getParameter("isbn");
        if (isbnStr != null && !isbnStr.trim().isBlank()) {
            try {
                data.isbn = Integer.parseInt(isbnStr.trim());
            } catch (NumberFormatException e) {
                data.errorMessage = "Mã ISBN phải là số nguyên hợp lệ trong giới hạn INTEGER.";
                return data;
            }
        }

        // 4. Publisher (Optional, varchar(100))
        String publisher = request.getParameter("publisher");
        if (publisher != null && !publisher.trim().isBlank()) {
            data.publisher = publisher.trim();
            if (data.publisher.length() > 100) {
                data.errorMessage = "Tên nhà xuất bản không được vượt quá 100 ký tự.";
                return data;
            }
        }

        // 5. Price (Optional, decimal(6,2))
        String priceStr = request.getParameter("price");
        if (priceStr != null && !priceStr.trim().isBlank()) {
            try {
                data.price = new BigDecimal(priceStr.trim());
                if (data.price.compareTo(BigDecimal.ZERO) < 0) {
                    data.errorMessage = "Giá sách không được là số âm.";
                    return data;
                }
                if (data.price.compareTo(MAX_DECIMAL_PRICE) > 0) {
                    data.errorMessage = "Giá sách vượt quá giới hạn DECIMAL(6,2) (tối đa 9999.99).";
                    return data;
                }
            } catch (NumberFormatException e) {
                data.errorMessage = "Giá sách không đúng định dạng số thập phân.";
                return data;
            }
        }

        // 6. Description (Optional, text)
        String desc = request.getParameter("description");
        if (desc != null) {
            data.description = desc.trim();
        }

        // 7. Publish Date (Optional, date: YYYY-MM-DD)
        String pubDateStr = request.getParameter("publishDate");
        if (pubDateStr != null && !pubDateStr.trim().isBlank()) {
            try {
                data.publishDate = LocalDate.parse(pubDateStr.trim());
            } catch (DateTimeParseException e) {
                data.errorMessage = "Ngày xuất bản không hợp lệ (định dạng YYYY-MM-DD).";
                return data;
            }
        }

        // 8. Cover Image (Optional, varchar(100))
        String coverImage = request.getParameter("coverImage");
        if (coverImage != null && !coverImage.trim().isBlank()) {
            data.coverImage = coverImage.trim();
            if (data.coverImage.length() > 100) {
                data.errorMessage = "Đường dẫn ảnh bìa không được vượt quá 100 ký tự.";
                return data;
            }
        } else {
            data.coverImage = "assets/images/book-placeholder.svg";
        }

        // 9. Quantity (Optional, int >= 0)
        String quantityStr = request.getParameter("quantity");
        if (quantityStr != null && !quantityStr.trim().isBlank()) {
            try {
                data.quantity = Integer.parseInt(quantityStr.trim());
                if (data.quantity < 0) {
                    data.errorMessage = "Số lượng sách trong kho không được âm.";
                    return data;
                }
            } catch (NumberFormatException e) {
                data.errorMessage = "Số lượng sách phải là số nguyên hợp lệ.";
                return data;
            }
        } else {
            data.quantity = 0;
        }

        return data;
    }

    private void preserveFormInput(HttpServletRequest request) {
        request.setAttribute("paramTitle", request.getParameter("title"));
        request.setAttribute("paramIsbn", request.getParameter("isbn"));
        request.setAttribute("selectedAuthorId", request.getParameter("authorId"));
        request.setAttribute("paramPublisher", request.getParameter("publisher"));
        request.setAttribute("paramPrice", request.getParameter("price"));
        request.setAttribute("paramDescription", request.getParameter("description"));
        request.setAttribute("paramPublishDate", request.getParameter("publishDate"));
        request.setAttribute("paramCoverImage", request.getParameter("coverImage"));
        request.setAttribute("paramQuantity", request.getParameter("quantity"));
    }

    private static final class ParsedBookData_24133023 {
        String errorMessage;
        Integer isbn;
        String title;
        int authorId;
        String publisher;
        BigDecimal price;
        String description;
        LocalDate publishDate;
        String coverImage;
        Integer quantity;
    }
}
