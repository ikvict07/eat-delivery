package com.delivery.customer.security;

import com.delivery.customer.jwt.JwtCore;
import com.delivery.customer.service.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenFilter extends OncePerRequestFilter {
    private JwtCore jwtCore;
    private UserServiceImpl userService;

    @Autowired
    private void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }

    @Autowired
    private void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwt(request);
            if (jwt != null) {
                String email = getEmail(jwtCore, jwt);
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    authenticate(userService, email);
                }
            }
        } catch (Exception ignored) {}
        filterChain.doFilter(request, response);
    }

    private static String getJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private static String getEmail(JwtCore jwtCore, String jwt) {
        String email = null;
        try {
            email = jwtCore.getEmailFromJwt(jwt);
        } catch (ExpiredJwtException ignored) {}
        return email;
    }

    private static void authenticate(UserServiceImpl userService, String email) {
        UserDetails userDetails = userService.loadUserByUsername(email);
        SecurityContextHolder
                .getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null));
    }
}
