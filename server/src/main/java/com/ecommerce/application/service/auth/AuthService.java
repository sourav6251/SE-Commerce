package com.ecommerce.application.service.auth;

import com.ecommerce.adapter.in.web.auth.dto.SignupDTO;
import com.ecommerce.adapter.in.web.auth.dto.UserDTO;
import com.ecommerce.adapter.out.persistence.enums.Role;
import com.ecommerce.adapter.out.persistence.enums.Status;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.UserRepository;
import com.ecommerce.application.port.in.auth.AuthUseCase;
import com.ecommerce.application.port.out.persistence.UserPort;
import com.ecommerce.domain.exception.InvalidCredentialsException;
import com.ecommerce.domain.exception.UserAlreadyExistException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthUseCase {

    private final PasswordEncoder passwordEncoder;
    private final UserPort userPort;

    public AuthService(PasswordEncoder passwordEncoder,UserPort userPort) {
        this.passwordEncoder = passwordEncoder;
        this.userPort = userPort;
    }

    @Override
    public UserDTO signup(SignupDTO signupDTO) {

        if (userPort.isUserExistByEmail(signupDTO.getEmail())) {
            throw new UserAlreadyExistException("User exists with this email");
        }

        UserEntity user = UserEntity.builder()
                .email(signupDTO.getEmail())
                .firstName(signupDTO.getName())
                .password(passwordEncoder.encode(signupDTO.getPassword()))
                .phoneNumber(signupDTO.getPhone())
                .role(Role.CUSTOMER)
                .status(Status.ACTIVE)
                .build();

        return userPort.saveUser(user);
    }

    @Override
    public UserEntity login(SignupDTO signupDTO) {

        if (userPort.isUserExistByEmail(signupDTO.getEmail())) {
            throw new UserAlreadyExistException("User exists with this email");
        }

       UserEntity user= userPort.findUserByEmail(signupDTO.getEmail());

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
