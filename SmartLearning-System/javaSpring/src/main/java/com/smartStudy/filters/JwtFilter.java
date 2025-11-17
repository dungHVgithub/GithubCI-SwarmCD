/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.filters;

import com.smartStudy.untils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author AN515-57
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String uri = request.getRequestURI();

        // 1) Bỏ qua các endpoint public
        if (uri.equals("/api/google-login") || uri.equals("/api/login")
                || uri.startsWith("/public/") || uri.startsWith("/swagger") || uri.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Lấy header Authorization
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) Lấy token & loại các giá trị rác
        String token = auth.substring(7).trim(); // sau "Bearer "
        if (token.isBlank() || "undefined".equalsIgnoreCase(token) || "null".equalsIgnoreCase(token) || !token.contains(".")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4) Validate JWT
        String username = null;
        try {
            username = JwtUtils.validateTokenAndGetUsername(token);
        } catch (Exception ignore) {
            // Không chặn, cho qua để Security match 401 ở chỗ khác nếu cần
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var authToken = new UsernamePasswordAuthenticationToken(
                    username, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
