package vn.hcmute.hnhbookstore.model;

import java.io.Serializable;
import java.math.BigDecimal;

public final class OrderDetail_24133023 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int detailId;
    private int orderId;
    private int bookId;
    private String bookTitle;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String coverImage;

    public OrderDetail_24133023() {
    }

    public OrderDetail_24133023(int detailId, int orderId, int bookId, String bookTitle,
                                int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        this.detailId = detailId;
        this.orderId = orderId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
