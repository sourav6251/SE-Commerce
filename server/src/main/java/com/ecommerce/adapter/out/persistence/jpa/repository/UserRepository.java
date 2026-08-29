package com.ecommerce.adapter.out.persistence.jpa.repository;

import com.ecommerce.adapter.out.persistence.jpa.entity.OtpEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

//    UserEntity save

}
