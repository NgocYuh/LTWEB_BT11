package vn.hcmute.hnhbookstore.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class Order_24133023 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int orderId;
    private int userId;
    private String customerName;
    private String phone;
    private String shippingAddress;
    private String note;
    private BigDecimal totalAmount;
    private String paymentMethod = "COD";
    private String status = "PENDING";
    private LocalDateTime createdAt;
    private List<OrderDetail_24133023> items = new ArrayList<>();

    public Order_24133023() {
    }

    public Order_24133023(int orderId, int userId, String customerName, String phone,
                          String shippingAddress, String note, BigDecimal totalAmount,
                          String paymentMethod, String status, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.customerName = customerName;
        this.phone = phone;
        this.shippingAddress = shippingAddress;
        this.note = note;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderDetail_24133023> getItems() {
        return items;
    }

    public void setItems(List<OrderDetail_24133023> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }
}
