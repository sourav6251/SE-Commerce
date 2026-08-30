package com.ecommerce.domain.exception;

public class MediaTypeException extends RuntimeException {
    public MediaTypeException(String message) {
        super(message);
    }
    public MediaTypeException() {
        super("Unsupported media type");
    }
}
