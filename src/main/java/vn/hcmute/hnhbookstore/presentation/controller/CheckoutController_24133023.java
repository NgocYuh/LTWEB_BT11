package vn.hcmute.hnhbookstore.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.hcmute.hnhbookstore.data.OrderDao_24133023;
import vn.hcmute.hnhbookstore.model.Cart_24133023;
import vn.hcmute.hnhbookstore.model.Order_24133023;
import vn.hcmute.hnhbookstore.model.User_24133023;
import vn.hcmute.hnhbookstore.util.CsrfUtil_24133023;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {
        "/checkout",
        "/checkout/place-order",
        "/checkout/success"
})
public final class CheckoutController_24133023 extends HttpServlet {
    private final OrderDao_24133023 orderDao = new OrderDao_24133023();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        HttpSession session = request.getSession(false);
        User_24133023 currentUser = (session != null) ? (User_24133023) session.getAttribute("currentUser") : null;

        // Requirement: Phải đăng nhập mới được checkout
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?required=1");
            return;
        }

        if ("/checkout".equals(path)) {
            Cart_24133023 cart = (Cart_24133023) session.getAttribute("cart");
            if (cart == null || cart.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            CsrfUtil_24133023.getOrCreateToken(session);

            // Điền sẵn thông tin nếu form chưa có dữ liệu từ lần nhập trước
            if (request.getAttribute("customerName") == null) {
                request.setAttribute("customerName", currentUser.getFullname());
            }
            if (request.getAttribute("phone") == null) {
                String phoneStr = "";
                if (currentUser.getPhone() != null) {
                    phoneStr = "0" + currentUser.getPhone();
                }
                request.setAttribute("phone", phoneStr);
            }

            request.getRequestDispatcher("/WEB-INF/views/checkout/checkout.jsp").forward(request, response);
        } else if ("/checkout/success".equals(path)) {
            String orderIdParam = request.getParameter("orderId");
            if (orderIdParam == null || orderIdParam.isBlank()) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            try {
                int orderId = Integer.parseInt(orderIdParam);
                Order_24133023 order = orderDao.findById(orderId);
                if (order == null || (order.getUserId() != currentUser.getId() && !currentUser.isAdmin())) {
                    response.sendRedirect(request.getContextPath() + "/home");
                    return;
                }

                request.setAttribute("order", order);
                request.getRequestDispatcher("/WEB-INF/views/checkout/success.jsp").forward(request, response);
            } catch (NumberFormatException | SQLException e) {
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        HttpSession session = request.getSession(false);
        User_24133023 currentUser = (session != null) ? (User_24133023) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?required=1");
            return;
        }

        if ("/checkout/place-order".equals(path)) {
            processPlaceOrder(request, response, session, currentUser);
        } else {
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void processPlaceOrder(HttpServletRequest request, HttpServletResponse response,
                                  HttpSession session, User_24133023 currentUser)
            throws ServletException, IOException {
        Cart_24133023 cart = (Cart_24133023) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if (!CsrfUtil_24133023.validateToken(request)) {
            request.setAttribute("errorMessage", "Phiên bảo mật đã hết hạn hoặc yêu cầu không hợp lệ. Vui lòng thử lại.");
            forwardToCheckout(request, response);
            return;
        }

        String customerName = request.getParameter("customerName");
        String phone = request.getParameter("phone");
        String shippingAddress = request.getParameter("shippingAddress");
        String note = request.getParameter("note");

        request.setAttribute("customerName", customerName);
        request.setAttribute("phone", phone);
        request.setAttribute("shippingAddress", shippingAddress);
        request.setAttribute("note", note);

        // Validation
        if (customerName == null || customerName.trim().length() < 2 || customerName.trim().length() > 100) {
            request.setAttribute("errorMessage", "Họ và tên người nhận phải có từ 2 đến 100 ký tự.");
            forwardToCheckout(request, response);
            return;
        }

        if (phone == null || !phone.trim().matches("^(0|\\+84)[0-9]{9,10}$")) {
            request.setAttribute("errorMessage", "Số điện thoại nhận hàng không hợp lệ (ví dụ: 0912345678).");
            forwardToCheckout(request, response);
            return;
        }

        if (shippingAddress == null || shippingAddress.trim().length() < 5 || shippingAddress.trim().length() > 255) {
            request.setAttribute("errorMessage", "Địa chỉ nhận hàng phải chi tiết và có độ dài từ 5 đến 255 ký tự.");
            forwardToCheckout(request, response);
            return;
        }

        try {
            Order_24133023 order = new Order_24133023();
            order.setUserId(currentUser.getId());
            order.setCustomerName(customerName.trim());
            order.setPhone(phone.trim());
            order.setShippingAddress(shippingAddress.trim());
            order.setNote((note != null) ? note.trim() : "");
            order.setTotalAmount(cart.getTotalAmount());
            order.setPaymentMethod("COD");
            order.setStatus("PENDING");

            int orderId = orderDao.createOrderWithDetails(order, cart.getItems());

            // Xóa sạch giỏ hàng khi đặt hàng thành công
            cart.clear();

            response.sendRedirect(request.getContextPath() + "/checkout/success?orderId=" + orderId);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", e.getMessage());
            forwardToCheckout(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tạo đơn hàng: " + e.getMessage());
            forwardToCheckout(request, response);
        }
    }

    private void forwardToCheckout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/checkout/checkout.jsp").forward(request, response);
    }
}
