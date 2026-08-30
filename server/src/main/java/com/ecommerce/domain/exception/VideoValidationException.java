package com.ecommerce.domain.exception;

public class VideoValidationException extends RuntimeException {
    public VideoValidationException(String message) {
        super(message);
    }
    public VideoValidationException() {
        super("Video Validation Exception");
    }
}
