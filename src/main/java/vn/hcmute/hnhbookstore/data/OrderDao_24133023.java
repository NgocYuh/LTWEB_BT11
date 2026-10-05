package vn.hcmute.hnhbookstore.data;

import vn.hcmute.hnhbookstore.model.CartItem_24133023;
import vn.hcmute.hnhbookstore.model.OrderDetail_24133023;
import vn.hcmute.hnhbookstore.model.Order_24133023;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public final class OrderDao_24133023 {

    /**
     * Executes order creation in an atomic transaction:
     * 1. Checks available inventory for all items.
     * 2. Inserts order into dbo.orders.
     * 3. Inserts each item into dbo.order_details.
     * 4. Deducts stock in dbo.books.
     * Rollbacks if anything fails or stock is insufficient.
     */
    public int createOrderWithDetails(Order_24133023 order, List<CartItem_24133023> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất một sản phẩm.");
        }

        Connection conn = null;
        try {
            conn = ConnectionFactory_24133023.open();
            conn.setAutoCommit(false);

            // Step 1: Check stock
            String checkStockSql = "SELECT quantity, title FROM dbo.books WHERE bookid = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkStockSql)) {
                for (CartItem_24133023 item : items) {
                    checkStmt.setInt(1, item.getBook().getBookid());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Không tìm thấy sách mã #" + item.getBook().getBookid());
                        }
                        int currentStock = rs.getInt("quantity");
                        String title = rs.getString("title");
                        if (currentStock < item.getQuantity()) {
                            throw new SQLException("Sách \"" + title + "\" không đủ số lượng tồn kho (chỉ còn " + currentStock + " cuốn, bạn yêu cầu " + item.getQuantity() + " cuốn).");
                        }
                    }
                }
            }

            // Step 2: Insert into dbo.orders
            String insertOrderSql = """
                INSERT INTO dbo.orders (user_id, customer_name, phone, shipping_address, note, total_amount, payment_method, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())
                """;
            int generatedOrderId;
            try (PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, order.getUserId());
                orderStmt.setNString(2, order.getCustomerName().trim());
                orderStmt.setString(3, order.getPhone().trim());
                orderStmt.setNString(4, order.getShippingAddress().trim());
                if (order.getNote() != null && !order.getNote().isBlank()) {
                    orderStmt.setNString(5, order.getNote().trim());
                } else {
                    orderStmt.setNull(5, Types.NVARCHAR);
                }
                orderStmt.setBigDecimal(6, order.getTotalAmount());
                orderStmt.setString(7, order.getPaymentMethod() != null ? order.getPaymentMethod() : "COD");
                orderStmt.setString(8, order.getStatus() != null ? order.getStatus() : "PENDING");

                int affected = orderStmt.executeUpdate();
                if (affected == 0) {
                    throw new SQLException("Tạo đơn hàng thất bại, không có bản ghi nào được thêm.");
                }

                try (ResultSet keys = orderStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedOrderId = keys.getInt(1);
                        order.setOrderId(generatedOrderId);
                    } else {
                        throw new SQLException("Tạo đơn hàng thất bại, không lấy được mã đơn hàng.");
                    }
                }
            }

            // Step 3: Insert into dbo.order_details
            String insertDetailSql = """
                INSERT INTO dbo.order_details (order_id, bookid, book_title, quantity, unit_price, subtotal)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
            try (PreparedStatement detailStmt = conn.prepareStatement(insertDetailSql)) {
                for (CartItem_24133023 item : items) {
                    detailStmt.setInt(1, generatedOrderId);
                    detailStmt.setInt(2, item.getBook().getBookid());
                    detailStmt.setString(3, item.getBook().getTitle());
                    detailStmt.setInt(4, item.getQuantity());
                    detailStmt.setBigDecimal(5, item.getBook().getPrice());
                    detailStmt.setBigDecimal(6, item.getTotalPrice());
                    detailStmt.addBatch();
                }
                detailStmt.executeBatch();
            }

            // Step 4: Deduct stock in dbo.books
            String updateStockSql = "UPDATE dbo.books SET quantity = quantity - ? WHERE bookid = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
                for (CartItem_24133023 item : items) {
                    updateStmt.setInt(1, item.getQuantity());
                    updateStmt.setInt(2, item.getBook().getBookid());
                    updateStmt.addBatch();
                }
                updateStmt.executeBatch();
            }

            conn.commit();
            return generatedOrderId;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }
            }
            if (e instanceof SQLException sqlEx) {
                throw sqlEx;
            }
            throw new SQLException("Lỗi xử lý đặt hàng: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    public Order_24133023 findById(int orderId) throws SQLException {
        String sql = """
            SELECT order_id, user_id, customer_name, phone, shipping_address, note, total_amount, payment_method, status, created_at
            FROM dbo.orders
            WHERE order_id = ?
            """;
        try (Connection conn = ConnectionFactory_24133023.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order_24133023 order = mapOrder(rs);
                    order.setItems(findDetailsByOrderId(orderId));
                    return order;
                }
            }
        }
        return null;
    }

    public List<OrderDetail_24133023> findDetailsByOrderId(int orderId) throws SQLException {
        String sql = """
            SELECT detail_id, order_id, bookid, book_title, quantity, unit_price, subtotal
            FROM dbo.order_details
            WHERE order_id = ?
            ORDER BY detail_id ASC
            """;
        List<OrderDetail_24133023> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory_24133023.open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetail_24133023 d = new OrderDetail_24133023();
                    d.setDetailId(rs.getInt("detail_id"));
                    d.setOrderId(rs.getInt("order_id"));
                    d.setBookId(rs.getInt("bookid"));
                    d.setBookTitle(rs.getString("book_title"));
                    d.setQuantity(rs.getInt("quantity"));
                    d.setUnitPrice(rs.getBigDecimal("unit_price"));
                    d.setSubtotal(rs.getBigDecimal("subtotal"));
                    list.add(d);
                }
            }
        }
        return list;
    }

    private Order_24133023 mapOrder(ResultSet rs) throws SQLException {
        Order_24133023 o = new Order_24133023();
        o.setOrderId(rs.getInt("order_id"));
        o.setUserId(rs.getInt("user_id"));
        o.setCustomerName(rs.getNString("customer_name"));
        o.setPhone(rs.getString("phone"));
        o.setShippingAddress(rs.getNString("shipping_address"));
        o.setNote(rs.getNString("note"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            o.setCreatedAt(ts.toLocalDateTime());
        }
        return o;
    }
}
