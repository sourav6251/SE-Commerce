package com.ecommerce.application.service.auth;

import com.ecommerce.adapter.in.web.auth.dto.SignupDTO;
import com.ecommerce.adapter.out.persistence.enums.Role;
import com.ecommerce.adapter.out.persistence.enums.Status;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.UserRepository;
import com.ecommerce.application.port.in.auth.AuthUseCase;
import com.ecommerce.domain.exception.InvalidCredentialsException;
import com.ecommerce.domain.exception.UserAlreadyExist;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity signup(SignupDTO signupDTO) {

        userRepository.findByEmail(signupDTO.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyExist("User exists with this email");
                });

        UserEntity user = UserEntity.builder()
                .email(signupDTO.getEmail())
                .firstName(signupDTO.getName())
                .password(passwordEncoder.encode(signupDTO.getPassword()))
                .phoneNumber(signupDTO.getPhone())
                .role(Role.CUSTOMER)
                .status(Status.ACTIVE)
                .build();

        return userRepository.save(user);
    }

    @Override
    public UserEntity login(SignupDTO signupDTO) {

        // 1. Find user by email
        UserEntity user = userRepository.findByEmail(signupDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // 2. Verify BCrypt password
        if (!passwordEncoder.matches(signupDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // 3. Verify user status
        if (user.getStatus() != null && user.getStatus() == Status.SUSPENDED) {
            throw new InvalidCredentialsException("Your account is suspended. Please contact support.");
        }

        return user;
    }
}
