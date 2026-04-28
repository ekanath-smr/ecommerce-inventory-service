package com.example.ecommerce_inventory_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

//        log.info("JWT Filter hit for path: {}", request.getRequestURI());
        String path = request.getRequestURI();
        String method = request.getMethod();
        String authHeader = request.getHeader("Authorization");
//        log.info("Authorization header received: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            if (jwtService.validateToken(token)) {
                String tokenType = jwtService.extractTokenType(token);
                if (!"ACCESS".equals(tokenType)) {
                    log.warn("Invalid token type | method={} | path={}", method, path);
                    filterChain.doFilter(request, response);
                    return;
                }
                String username = jwtService.extractUsername(token);
                List<SimpleGrantedAuthority> authorities = jwtService.extractRoles(token)
                        .stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();
//                log.info("Authenticated user: {}", username);
//                log.info("Authorities: {}", authorities);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities
                        );
                authentication.setDetails(
                        new org.springframework.security.web.authentication.WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT authenticated | user={} | method={} | path={}", username, method, path);
            }
        } catch (Exception ex) {
            log.error("JWT authentication failed | method={} | path={}", method, path, ex);
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}