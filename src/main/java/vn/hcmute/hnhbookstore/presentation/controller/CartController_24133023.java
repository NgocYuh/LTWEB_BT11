package vn.hcmute.hnhbookstore.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.hcmute.hnhbookstore.data.BookDao_24133023;
import vn.hcmute.hnhbookstore.model.Book_24133023;
import vn.hcmute.hnhbookstore.model.Cart_24133023;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {
        "/cart",
        "/cart/add",
        "/cart/update",
        "/cart/remove",
        "/cart/clear"
})
public final class CartController_24133023 extends HttpServlet {
    private final BookDao_24133023 bookDao = new BookDao_24133023();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/cart".equals(path)) {
            getOrCreateCart(request.getSession(true));
            request.getRequestDispatcher("/WEB-INF/views/cart/index.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        HttpSession session = request.getSession(true);
        Cart_24133023 cart = getOrCreateCart(session);

        if ("/cart/add".equals(path)) {
            processAddToCart(request, response, cart);
        } else if ("/cart/update".equals(path)) {
            processUpdateCart(request, response, cart);
        } else if ("/cart/remove".equals(path)) {
            processRemoveItem(request, response, cart);
        } else if ("/cart/clear".equals(path)) {
            cart.clear();
            response.sendRedirect(request.getContextPath() + "/cart?msg=cleared");
        } else {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void processAddToCart(HttpServletRequest request, HttpServletResponse response, Cart_24133023 cart)
            throws IOException {
        String bookIdParam = request.getParameter("bookId");
        String qtyParam = request.getParameter("quantity");
        String returnUrl = request.getParameter("returnUrl");

        int bookId;
        int qty = 1;
        try {
            bookId = Integer.parseInt(bookIdParam);
            if (qtyParam != null && !qtyParam.isBlank()) {
                qty = Integer.parseInt(qtyParam);
                if (qty < 1) qty = 1;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cart?error=invalid_param");
            return;
        }

        try {
            Book_24133023 book = bookDao.findById(bookId);
            if (book == null) {
                response.sendRedirect(request.getContextPath() + "/cart?error=not_found");
                return;
            }

            int stock = (book.getQuantity() != null) ? book.getQuantity() : 0;
            if (stock <= 0) {
                if (returnUrl != null && !returnUrl.isBlank()) {
                    response.sendRedirect(returnUrl + (returnUrl.contains("?") ? "&" : "?") + "cart_err=out_of_stock");
                } else {
                    response.sendRedirect(request.getContextPath() + "/cart?error=out_of_stock");
                }
                return;
            }

            cart.addItem(book, qty);

            if (returnUrl != null && !returnUrl.isBlank()) {
                response.sendRedirect(returnUrl + (returnUrl.contains("?") ? "&" : "?") + "cart_msg=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/cart?msg=added");
            }
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/cart?error=db_error");
        }
    }

    private void processUpdateCart(HttpServletRequest request, HttpServletResponse response, Cart_24133023 cart)
            throws IOException {
        String bookIdParam = request.getParameter("bookId");
        String qtyParam = request.getParameter("quantity");

        int bookId;
        int qty;
        try {
            bookId = Integer.parseInt(bookIdParam);
            qty = Integer.parseInt(qtyParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cart?error=invalid_param");
            return;
        }

        try {
            Book_24133023 book = bookDao.findById(bookId);
            int stock = (book != null && book.getQuantity() != null) ? book.getQuantity() : 0;
            cart.updateQuantity(bookId, qty, stock);
            response.sendRedirect(request.getContextPath() + "/cart?msg=updated");
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/cart?error=db_error");
        }
    }

    private void processRemoveItem(HttpServletRequest request, HttpServletResponse response, Cart_24133023 cart)
            throws IOException {
        String bookIdParam = request.getParameter("bookId");
        try {
            int bookId = Integer.parseInt(bookIdParam);
            cart.removeItem(bookId);
            response.sendRedirect(request.getContextPath() + "/cart?msg=removed");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cart?error=invalid_param");
        }
    }

    private Cart_24133023 getOrCreateCart(HttpSession session) {
        Cart_24133023 cart = (Cart_24133023) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart_24133023();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}
