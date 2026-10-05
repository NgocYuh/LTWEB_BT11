package vn.hcmute.hnhbookstore.business;

import vn.hcmute.hnhbookstore.data.AuthorDao_24133023;
import vn.hcmute.hnhbookstore.data.BookDao_24133023;
import vn.hcmute.hnhbookstore.model.AuthorGroup_24133023;
import vn.hcmute.hnhbookstore.model.Author_24133023;
import vn.hcmute.hnhbookstore.model.Book_24133023;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CatalogService_24133023 {
    public static final int BOOKS_PER_AUTHOR_PAGE = 3;

    private final AuthorDao_24133023 authorDao;
    private final BookDao_24133023 bookDao;

    public CatalogService_24133023() {
        this(new AuthorDao_24133023(), new BookDao_24133023());
    }

    public CatalogService_24133023(AuthorDao_24133023 authorDao, BookDao_24133023 bookDao) {
        this.authorDao = authorDao;
        this.bookDao = bookDao;
    }

    public List<AuthorGroup_24133023> getAuthorCatalog(Map<Integer, Integer> authorPageMap) throws SQLException {
        List<Author_24133023> authors = authorDao.findAll();
        List<AuthorGroup_24133023> groups = new ArrayList<>();

        for (Author_24133023 author : authors) {
            int authorId = author.getAuthorId();
            int totalBooks = bookDao.countBooksByAuthor(authorId);
            int totalPages = (int) Math.ceil((double) totalBooks / BOOKS_PER_AUTHOR_PAGE);
            if (totalPages < 1) totalPages = 1;

            int requestedPage = authorPageMap.getOrDefault(authorId, 1);
            int validPage = Math.max(1, Math.min(requestedPage, totalPages));

            List<Book_24133023> books = bookDao.findBooksByAuthor(authorId, validPage, BOOKS_PER_AUTHOR_PAGE);
            groups.add(new AuthorGroup_24133023(author, books, validPage, totalPages, totalBooks));
        }

        return groups;
    }

    public Book_24133023 getBookDetail(int bookId) throws SQLException {
        return bookDao.findById(bookId);
    }
}
