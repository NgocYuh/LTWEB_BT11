package vn.hcmute.hnhbookstore.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.hcmute.hnhbookstore.data.OrderDao_24133023;
import vn.hcmute.hnhbookstore.model.Order_24133023;
import vn.hcmute.hnhbookstore.model.User_24133023;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/orders",
        "/orders/history"
})
public final class OrderHistoryController_24133023 extends HttpServlet {
    private final OrderDao_24133023 orderDao = new OrderDao_24133023();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User_24133023 currentUser = (session != null) ? (User_24133023) session.getAttribute("currentUser") : null;

        // Phải đăng nhập mới xem được lịch sử đơn hàng
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?required=1");
            return;
        }

        String statusParam = request.getParameter("status");
        String activeStatus = Order_24133023.normalizeStatusKey(statusParam);

        // Quản trị viên có thể xem tất cả đơn của hệ thống hoặc chuyển sang xem đơn của chính mình
        String scope = request.getParameter("scope");
        Integer queryUserId = currentUser.getId();
        if (currentUser.isAdmin() && !"my".equalsIgnoreCase(scope)) {
            queryUserId = null; // null = xem tất cả đơn hàng
        }

        try {
            Map<String, Integer> statusCounts = orderDao.countOrdersByStatus(queryUserId);
            List<Order_24133023> orders = orderDao.findOrders(queryUserId, activeStatus);

            request.setAttribute("orders", orders);
            request.setAttribute("statusCounts", statusCounts);
            request.setAttribute("activeStatus", activeStatus);
            request.setAttribute("scope", (queryUserId == null) ? "all" : "my");

            request.getRequestDispatcher("/WEB-INF/views/orders/index.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Không thể tải danh sách đơn hàng: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/orders/index.jsp").forward(request, response);
        }
    }
}
