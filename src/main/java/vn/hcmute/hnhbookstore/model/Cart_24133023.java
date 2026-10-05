package vn.hcmute.hnhbookstore.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Cart_24133023 implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Integer, CartItem_24133023> items = new LinkedHashMap<>();

    public Cart_24133023() {
    }

    public synchronized int addItem(Book_24133023 book, int addQty) {
        if (book == null || addQty <= 0) {
            return 0;
        }

        int maxStock = (book.getQuantity() != null) ? book.getQuantity() : 0;
        if (maxStock <= 0) {
            return 0; // Hết hàng
        }

        int bookId = book.getBookid();
        CartItem_24133023 currentItem = items.get(bookId);
        int targetQty = addQty;

        if (currentItem != null) {
            targetQty = currentItem.getQuantity() + addQty;
        }

        if (targetQty > maxStock) {
            targetQty = maxStock;
        }

        if (currentItem != null) {
            currentItem.setQuantity(targetQty);
        } else {
            items.put(bookId, new CartItem_24133023(book, targetQty));
        }

        return targetQty;
    }

    public synchronized void updateQuantity(int bookId, int newQty, int maxStock) {
        if (!items.containsKey(bookId)) {
            return;
        }

        if (newQty <= 0) {
            items.remove(bookId);
            return;
        }

        if (newQty > maxStock) {
            newQty = maxStock;
        }

        CartItem_24133023 item = items.get(bookId);
        item.setQuantity(newQty);
    }

    public synchronized void removeItem(int bookId) {
        items.remove(bookId);
    }

    public synchronized void clear() {
        items.clear();
    }

    public synchronized List<CartItem_24133023> getItems() {
        return new ArrayList<>(items.values());
    }

    public synchronized int getTotalQuantity() {
        return items.values().stream()
                .mapToInt(CartItem_24133023::getQuantity)
                .sum();
    }

    public synchronized BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem_24133023 item : items.values()) {
            total = total.add(item.getTotalPrice());
        }
        return total;
    }

    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }
}
