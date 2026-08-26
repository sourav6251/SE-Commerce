package com.ecommerce.application.port.in.otp;

public interface OtpUseCase {

    void generate(String email);

    void verify(String email, String otp);
}
