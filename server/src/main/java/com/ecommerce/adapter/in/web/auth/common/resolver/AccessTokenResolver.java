package com.ecommerce.adapter.in.web.auth.common.resolver;


import com.ecommerce.annotation.AuthToken;
import com.ecommerce.config.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AccessTokenResolver implements HandlerMethodArgumentResolver {
    private final JWTService jwtService;

    public AccessTokenResolver(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthToken.class)
                && parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        AuthToken annotation = parameter.getParameterAnnotation(AuthToken.class);
        if (annotation == null) {
            return null;
        }

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return null;
        }

        String token = null;
        String value = annotation.value();
        String headerName = value.isEmpty() ? annotation.headerName() : value;
        String cookieName = value.isEmpty() ? annotation.cookieName() : value;

        // 1. Check Header
//        String headerValue = request.getHeader(headerName);
//        if (headerValue != null) {
//            if ("Authorization".equalsIgnoreCase(headerName) && headerValue.toLowerCase().startsWith("bearer ")) {
//                token = headerValue.substring(7).trim();
//            } else {
//                token = headerValue.trim();
//            }
//        }

        // 2. Check Cookie if not found in header
        token = jwtService.extractToken(request, cookieName);
//            Cookie[] cookies = request.getCookies();
//            if (cookies != null) {
//                for (Cookie cookie : cookies) {
//                    if (cookieName.equals(cookie.getName())) {
//                        token = cookie.getValue();
//                        break;
//                    }
//                }
//            }

        // 3. Handle required constraint
        if ((token == null || token.isEmpty()) && annotation.required()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing access token in header or cookie");
        }

        return token;
    }
}
