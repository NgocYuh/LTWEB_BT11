package vn.hcmute.hnhbookstore.model;

import java.io.Serializable;
import java.math.BigDecimal;

public final class CartItem_24133023 implements Serializable {
    private static final long serialVersionUID = 1L;

    private Book_24133023 book;
    private int quantity;

    public CartItem_24133023() {
    }

    public CartItem_24133023(Book_24133023 book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public Book_24133023 getBook() {
        return book;
    }

    public void setBook(Book_24133023 book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        if (book == null || book.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return book.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
