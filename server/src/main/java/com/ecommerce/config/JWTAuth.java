package com.ecommerce.config;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
public class JWTAuth extends OncePerRequestFilter {

    private final JWTService jwtService;
//    private final UserDetailsService userDetailsService;

    public JWTAuth(JWTService jwtCreate) {
        this.jwtService = jwtCreate;
//        this.userDetailsService = userDetailsService;
    }

    /**
     *  CRITICAL: WebSocket handshake bypasses JWTAuth completely
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        //  Detect WebSocket handshake
//        boolean isWebSocketHandshake = isWebSocketHandshake(request);

        //  WebSocket endpoints
//        boolean isWebSocketEndpoint = path.startsWith("/ws");

        // TODO:Public endpoints
        boolean isPublicEndpoint = (path.startsWith("/auth/") && !path.equals("/auth/logout"))
                || path.startsWith("/public/")
                || path.startsWith("/user/")
                || path.equals("/user");

        //  Preflight requests
        boolean isPreflight = "OPTIONS".equalsIgnoreCase(method);

        // isWebSocketHandshake || isWebSocketEndpoint ||
        boolean shouldSkip = isPublicEndpoint || isPreflight;


//        if (isWebSocketHandshake) {
////            log.info("🔌 WEB SOCKET BYPASS - Handshake detected for: {} {}", method, path);
//        }

        return shouldSkip;
    }

    /**
     * 🛡 PROCESS HTTP REQUESTS ONLY (WebSocket never reaches here)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();


        logRequestDetails(request);

        try {
            // 🔐 1. Extract JWT Token
            String token = extractJwtToken(request);

            if (token != null) {
                // ✅ 2. Authenticate HTTP Request
                authenticateHttpRequest(token);
//                log.info("✅ JWTAuth SUCCESS - User authenticated for: {} {}", method, path);
            } else {
//                log.warn("⚠️ JWTAuth NO TOKEN - Proceeding anonymously for: {} {}", method, path);
            }

            // ➡️ 3. Continue to HTTP Controller
            filterChain.doFilter(request, response);
            log.debug("✅ JWTAuth Completed - Request forwarded to controller");

        } catch (ExpiredJwtException e) {
            handleError(response, "Token expired", HttpServletResponse.SC_UNAUTHORIZED, e);
        } catch (JwtException e) {
            handleError(response, "Invalid token", HttpServletResponse.SC_UNAUTHORIZED, e);
        } catch (UsernameNotFoundException e) {
            handleError(response, "User not found", HttpServletResponse.SC_BAD_REQUEST, e);
        } catch (DisabledException e) {
            handleError(response, "User is disabled", HttpServletResponse.SC_FORBIDDEN, e);
        } catch (LockedException e) {
            handleError(response, "User account is locked", HttpServletResponse.SC_FORBIDDEN, e);
        } catch (AccountExpiredException e) {
            handleError(response, "User account has expired", HttpServletResponse.SC_FORBIDDEN, e);
        } catch (CredentialsExpiredException e) {
            handleError(response, "User credentials have expired", HttpServletResponse.SC_FORBIDDEN, e);
        } catch (AuthenticationException e) {
            handleError(response, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED, e);
        } catch (Exception e) {
            handleError(response, "Authentication failed", HttpServletResponse.SC_UNAUTHORIZED, e);
        }
    }

    /**
     * 🔐 EXTRACT JWT TOKEN FROM HTTP REQUEST
     */
    private String extractJwtToken(HttpServletRequest request) {
        String token = null;

        // 1. Try Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.debug("🔑 Token from Authorization header: PRESENT ({} chars)", token.length());
        } else {
            log.debug("🔑 Token from Authorization header: NULL");
        }

        // 2. Try cookies if no header token
        if (token == null) {
            token = jwtService.extractAccessToken(request);
            log.debug("🍪 Token from cookies: {}", token != null ? "PRESENT" : "NULL");
        }

        return token;
    }

    /**
     * ✅ AUTHENTICATE HTTP REQUEST WITH JWT TOKEN (STATELESS - NO DB QUERY)
     */
    private void authenticateHttpRequest(String token) {
        log.debug("🔐 Validating JWT token for HTTP request...");

        // Validate token - throws ExpiredJwtException or JwtException if invalid
        jwtService.validateToken(token);

        // Extract username/userId from token
        String username = jwtService.getUserID(token);
        log.debug("👤 Token validated for user: {}", username);

        // Extract optional role claim
        String role = jwtService.getClaim(token, "role", String.class);
        java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
        if (role != null && !role.isEmpty()) {
            authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role));
        }

        // Create authentication object statelessly (without querying DB)
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities);

        // Set security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("✅ HTTP User authenticated statelessly: {} with roles: {}",
                username, authorities);
    }

    /**
     * 📊 LOG REQUEST DETAILS FOR DEBUGGING
     */
    private void logRequestDetails(HttpServletRequest request) {
//        log.debug("📨 HTTP Request Details:");
//        log.debug("   🌐 URL: {} {}", request.getMethod(), request.getRequestURL());
//        log.debug("   ❓ Query: {}", request.getQueryString());
//        log.debug("   📝 Content-Type: {}", request.getContentType());
//        log.debug("   📍 Origin: {}", request.getHeader("Origin"));
//        log.debug("   👤 User-Agent: {}", request.getHeader("User-Agent"));
//        log.debug("    Authorization Header: {}",
//                request.getHeader("Authorization") != null ? "PRESENT" : "NULL");
    }

    /**
     *  HANDLE AUTHENTICATION ERRORS
     */
    private void handleError(HttpServletResponse response, String message,
                             int statusCode, Exception e) throws IOException {
        log.error("❌ JWTAuth FAILED - Status: {}, Message: {}, Error: {}",
                statusCode, message, e.getMessage());

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"error\": \"%s\", \"message\": \"%s\", \"status\": %d}",
                e.getClass().getSimpleName(), message, statusCode
        );

        response.getWriter().write(jsonResponse);
    }
}
