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

import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login",
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
        boolean loggedIn = session != null && session.getAttribute("authUser") != null;

        if (loggedIn || isPublic) {
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
