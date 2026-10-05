package vn.hcmute.hnhbookstore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class AuthorGroup_24133023 implements Serializable {
    private static final long serialVersionUID = 1L;

    private Author_24133023 author;
    private List<Book_24133023> books = new ArrayList<>();
    private int currentPage;
    private int totalPages;
    private int totalBooks;

    public AuthorGroup_24133023() {
    }

    public AuthorGroup_24133023(Author_24133023 author, List<Book_24133023> books,
                                int currentPage, int totalPages, int totalBooks) {
        this.author = author;
        this.books = books;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalBooks = totalBooks;
    }

    public Author_24133023 getAuthor() {
        return author;
    }

    public void setAuthor(Author_24133023 author) {
        this.author = author;
    }

    public List<Book_24133023> getBooks() {
        return books;
    }

    public void setBooks(List<Book_24133023> books) {
        this.books = books;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public boolean hasPrevious() {
        return currentPage > 1;
    }

    public boolean hasNext() {
        return currentPage < totalPages;
    }
}

