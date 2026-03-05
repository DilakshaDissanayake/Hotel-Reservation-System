package com.example.hotelreservationsystem.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.example.hotelreservationsystem.model.User;

import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login",
            "/forgot-password",
            "/reset-password",
            "/assets",
            "/assets/"
    );

    private static final Set<String> PUBLIC_EXTENSIONS = Set.of(
            ".css",
            ".js",
            ".png",
            ".jpg",
            ".jpeg",
            ".svg",
            ".gif",
            ".woff",
            ".woff2",
            ".ttf",
            ".eot",
            ".ico",
            ".map"
    );

    private static final java.util.Set<String> ADMIN_ONLY_PATHS = java.util.Set.of(
            "/staff",
            "/rooms/edit"
    );

    private static final java.util.Set<String> ADMIN_ONLY_POST_PATHS = java.util.Set.of(
            "/rooms"
    );

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI()
                .substring(httpRequest.getContextPath().length());

        boolean isPublic = PUBLIC_PATHS.stream()
                .anyMatch(path::startsWith)
                || isStaticAsset(path);

        HttpSession session = httpRequest.getSession(false);
        User authUser = (session != null) ? (User) session.getAttribute("authUser") : null;
        boolean loggedIn = authUser != null;

        if (loggedIn) {
            String userRole = authUser.getRole();
            boolean isAdminPath = ADMIN_ONLY_PATHS.stream().anyMatch(path::startsWith);
            boolean isAdminPost = "POST".equalsIgnoreCase(httpRequest.getMethod()) &&
                                 ADMIN_ONLY_POST_PATHS.stream().anyMatch(path::startsWith);

            if ((isAdminPath || isAdminPost) && !"ADMIN".equals(userRole)) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/dashboard?error=Access%20Denied");
                return;
            }

            chain.doFilter(request, response);
        } else if (isPublic) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + "/login"
            );
        }
    }

    private boolean isStaticAsset(String path) {
        String lower = path.toLowerCase();
        return PUBLIC_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }
}
