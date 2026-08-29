package com.ecommerce.application.port.out.persistence;

import com.ecommerce.domain.auth.Otp;

import java.util.Optional;

public interface OtpPort {

    void save(String email, Otp otp);

    Optional<Otp> findActiveByEmail(String email);

    void markAsUsed(String email, String code);

    void deleteVerifiedUser(String email);
}

