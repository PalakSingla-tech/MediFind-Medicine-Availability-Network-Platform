package com.medifind.api_gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        /*
         * Auth endpoints such as:
         *
         * /api/auth/login
         * /api/auth/register
         *
         * don't need JWT authentication.
         */
        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        /*
         * Get Authorization header
         */
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        /*
         * JWT is missing
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        /*
         * Remove "Bearer "
         */
        String token = authHeader.substring(7);

        try {

            /*
             * This validates the JWT and extracts userId.
             *
             * If the JWT is expired or invalid,
             * an exception will be thrown.
             */
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            /*
             * Modify the request before sending it
             * to the downstream microservice.
             *
             * IMPORTANT:
             * Remove any X-User-Id supplied by the client.
             * Then set our trusted value from the JWT.
             */
            ServerWebExchange modifiedExchange =
                    exchange.mutate()
                            .request(
                                    exchange.getRequest()
                                            .mutate()
                                            .headers(headers -> {

                                                headers.remove("X-User-Id");
                                                headers.remove("X-User-Role");

                                                headers.set(
                                                        "X-User-Id",
                                                        userId.toString()
                                                );

                                                headers.set(
                                                        "X-User-Role",
                                                        role
                                                );
                                            })
                                            .build()
                            )
                            .build();

            /*
             * Continue the request with the modified request.
             */
            return chain.filter(modifiedExchange);

        } catch (Exception e) {

            /*
             * Invalid/expired JWT
             */
            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }
    }

    /*
     * Run this filter early.
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
