package com.ecommerce.domain.exception;

public class ImagePixelLimitException extends RuntimeException {
    public ImagePixelLimitException(String message) {
        super(message);
    }
    public ImagePixelLimitException() {
        super("Image Pixel Limit Error");
    }
}
