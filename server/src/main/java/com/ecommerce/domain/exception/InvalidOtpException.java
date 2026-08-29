package com.ecommerce.domain.exception;

public class InvalidOtpException extends RuntimeException {

    public InvalidOtpException(String message) {
        super(message);
    }
    public InvalidOtpException() {
        super("Invalid OTP");
    }
}
