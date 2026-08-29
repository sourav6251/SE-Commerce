package com.ecommerce.adapter.out.persistence.jpa.adapter;

import com.ecommerce.adapter.out.persistence.jpa.entity.OtpEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.OtpRepository;
import com.ecommerce.application.port.out.persistence.OtpPort;
import com.ecommerce.domain.auth.Otp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OtpPersistenceAdapter implements OtpPort {

    private final OtpRepository otpRepository;

    @Override
    public void save(String email, Otp otp) {
        OtpEntity entity = OtpEntity.builder()
                .email(email)
                .code(otp.getCode())
                .expiresAt(otp.getExpiresAt())
                .isUsed(false)
                .build();

        otpRepository.save(entity);
    }

    @Override
    public Optional<Otp> findActiveByEmail(String email) {
        return otpRepository.findFirstByEmailAndIsUsedFalseOrderByCreatedAtDesc(email)
                .map(entity -> Otp.of(entity.getCode(), entity.getExpiresAt()));
    }

    @Override
    public void markAsUsed(String email, String code) {
        otpRepository.findFirstByEmailAndCodeAndIsUsedFalse(email, code)
                .ifPresent(entity -> {
                    entity.setUsed(true);
                    otpRepository.save(entity);
                });
    }

    @Override
    public void deleteVerifiedUser(String email) {
        try {
            otpRepository.deleteByEmail(email);
        }catch (Exception e) {

        }
    }
}

