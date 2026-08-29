package com.ecommerce.adapter.out.persistence.jpa.repository;

import com.ecommerce.adapter.out.persistence.jpa.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {

    Optional<OtpEntity> findFirstByEmailAndIsUsedFalseOrderByCreatedAtDesc(String email);

    Optional<OtpEntity> findFirstByEmailAndCodeAndIsUsedFalse(String email, String code);

    void deleteByEmail(String email);
}
