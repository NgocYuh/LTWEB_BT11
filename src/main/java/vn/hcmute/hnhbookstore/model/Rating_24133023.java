package vn.hcmute.hnhbookstore.model;

import java.io.Serializable;

public final class Rating_24133023 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;
    private String userName;
    private int bookId;
    private Integer rating;
    private String reviewText;

    public Rating_24133023() {
    }

    public Rating_24133023(int userId, String userName, int bookId, Integer rating, String reviewText) {
        this.userId = userId;
        this.userName = userName;
        this.bookId = bookId;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}

