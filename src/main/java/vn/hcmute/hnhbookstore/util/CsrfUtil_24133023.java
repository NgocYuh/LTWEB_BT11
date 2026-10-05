package vn.hcmute.hnhbookstore.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class CsrfUtil_24133023 {
    public static final String CSRF_SESSION_ATTR = "csrf";
    public static final String CSRF_PARAM_NAME = "csrf";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil_24133023() {
    }

    public static String getOrCreateToken(HttpSession session) {
        if (session == null) {
            return "";
        }
        String token = (String) session.getAttribute(CSRF_SESSION_ATTR);
        if (token == null || token.isBlank()) {
            byte[] bytes = new byte[24];
            RANDOM.nextBytes(bytes);
            token = HexFormat.of().formatHex(bytes);
            session.setAttribute(CSRF_SESSION_ATTR, token);
        }
        return token;
    }

    public static boolean validateToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute(CSRF_SESSION_ATTR);
        String paramToken = request.getParameter(CSRF_PARAM_NAME);
        if (sessionToken == null || paramToken == null) {
            return false;
        }
        return sessionToken.equals(paramToken.trim());
    }
}

