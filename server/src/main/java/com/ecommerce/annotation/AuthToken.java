package com.ecommerce.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to extract the accessToken from either the Authorization header
 * (as Bearer token) or a Cookie.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthToken {
    /**
     * Shorthand to set both the header name and cookie name to the same value.
     */
    String value() default "";

    /**
     * The name of the cookie to look for. Defaults to "accessToken".
     */
    String cookieName() default "AccessToken";

    /**
     * The name of the header to look for. Defaults to "Authorization".
     */
    String headerName() default "Authorization";

    /**
     * Whether the token is required. If true and not found, an exception is thrown.
     */
    boolean required() default true;
}

