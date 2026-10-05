package vn.hcmute.hnhbookstore.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Book_24133023 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookid;
    private Integer isbn;
    private String title;
    private String publisher;
    private BigDecimal price;
    private String description;
    private LocalDate publishDate;
    private String coverImage;
    private Integer quantity;
    private List<Author_24133023> authors = new ArrayList<>();
    private int reviewsCount;

    public Book_24133023() {
    }

    public Book_24133023(int bookid, Integer isbn, String title, String publisher, BigDecimal price,
                         String description, LocalDate publishDate, String coverImage,
                         Integer quantity, int reviewsCount) {
        this.bookid = bookid;
        this.isbn = isbn;
        this.title = title;
        this.publisher = publisher;
        this.price = price;
        this.description = description;
        this.publishDate = publishDate;
        this.coverImage = coverImage;
        this.quantity = quantity;
        this.reviewsCount = reviewsCount;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public Integer getIsbn() {
        return isbn;
    }

    public void setIsbn(Integer isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<Author_24133023> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author_24133023> authors) {
        this.authors = authors;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public String getAuthorNames() {
        if (authors == null || authors.isEmpty()) {
            return "Đang cập nhật";
        }
        return authors.stream()
                .map(Author_24133023::getAuthorName)
                .collect(Collectors.joining(", "));
    }
}

