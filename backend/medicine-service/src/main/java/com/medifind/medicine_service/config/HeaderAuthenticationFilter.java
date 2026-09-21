package com.medifind.medicine_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final Pattern ROLE_PATTERN = Pattern.compile("\"role\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern USER_ID_PATTERN = Pattern.compile("\"userId\"\\s*:\\s*\"?(\\d+)\"?");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String roleHeader = request.getHeader("X-User-Role");
        String userIdHeader = request.getHeader("X-User-Id");

        // If gateway headers are not provided, attempt to extract role and userId from Authorization Bearer JWT
        if ((roleHeader == null || roleHeader.trim().isEmpty()) && (userIdHeader == null || userIdHeader.trim().isEmpty())) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                String[] parts = token.split("\\.");
                if (parts.length >= 2) {
                    try {
                        byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
                        String payload = new String(decodedBytes, StandardCharsets.UTF_8);

                        Matcher roleMatcher = ROLE_PATTERN.matcher(payload);
                        if (roleMatcher.find()) {
                            roleHeader = roleMatcher.group(1);
                        }

                        Matcher userMatcher = USER_ID_PATTERN.matcher(payload);
                        if (userMatcher.find()) {
                            userIdHeader = userMatcher.group(1);
                        }
                    } catch (Exception e) {
                        log.debug("Could not parse JWT payload: {}", e.getMessage());
                    }
                }
            }
        }

        if (roleHeader != null && !roleHeader.trim().isEmpty()) {
            String[] roles = roleHeader.split(",");
            List<GrantedAuthority> authorities = Arrays.stream(roles)
                    .map(String::trim)
                    .filter(r -> !r.isEmpty())
                    .map(r -> r.startsWith("ROLE_") ? r.toUpperCase() : "ROLE_" + r.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            String principal = (userIdHeader != null && !userIdHeader.trim().isEmpty())
                    ? userIdHeader.trim()
                    : "authenticated_user";

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userIdHeader.trim(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
