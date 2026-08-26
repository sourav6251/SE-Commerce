package com.ecommerce.domain.auth;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class Otp {

    private final String code;
    private final Instant expiresAt;

    private Otp(String code, Instant expiresAt) {
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public static Otp generate() {

        String code = String.format(
                "%06d",
                ThreadLocalRandom.current().nextInt(0, 1_000_000)
        );

        Instant expiresAt = Instant.now().plusSeconds(300);

        return new Otp(code, expiresAt);
    }

    public boolean isValid(String inputCode) {

        if (isExpired()) {
            return false;
        }

        return code.equals(inputCode);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public String getCode() {
        return code;
    }
}
