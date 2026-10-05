package vn.hcmute.hnhbookstore.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.hcmute.hnhbookstore.business.CatalogService_24133023;
import vn.hcmute.hnhbookstore.model.AuthorGroup_24133023;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/home", "/user/home"})
public final class HomeController_24133023 extends HttpServlet {
    private static final String AUTHOR_PAGES_SESSION_ATTR = "authorPagesMap";
    private final CatalogService_24133023 catalog = new CatalogService_24133023();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> pageMap = (Map<Integer, Integer>) session.getAttribute(AUTHOR_PAGES_SESSION_ATTR);
        if (pageMap == null) {
            pageMap = new HashMap<>();
            session.setAttribute(AUTHOR_PAGES_SESSION_ATTR, pageMap);
        }

        // Handle pagination query params: /home?authorId=1&page=2
        String authorIdParam = request.getParameter("authorId");
        String pageParam = request.getParameter("page");
        if (authorIdParam != null && pageParam != null) {
            try {
                int aId = Integer.parseInt(authorIdParam);
                int p = Integer.parseInt(pageParam);
                if (p < 1) p = 1;
                pageMap.put(aId, p);
            } catch (NumberFormatException ignored) {
            }
        }

        try {
            List<AuthorGroup_24133023> authorGroups = catalog.getAuthorCatalog(pageMap);
            request.setAttribute("authorGroups", authorGroups);
        } catch (SQLException exception) {
            getServletContext().log("Catalog unavailable; SQLState=" + exception.getSQLState(), exception);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("error", "Chưa thể tải thư viện sách. Vui lòng kiểm tra kết nối CSDL.");
        }

        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}
