package com.ecommerce.domain.exception;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException() {
        super("OTP has expired");
    }
}
