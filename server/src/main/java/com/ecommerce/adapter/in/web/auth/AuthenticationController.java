package com.ecommerce.adapter.in.web.auth;

import com.ecommerce.adapter.in.web.auth.dto.SignupDTO;
import com.ecommerce.adapter.in.web.auth.dto.UserDTO;
import com.ecommerce.adapter.out.persistence.enums.Role;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;
import com.ecommerce.annotation.AuthToken;
import com.ecommerce.application.port.in.auth.AuthUseCase;
import com.ecommerce.config.JWTService;
import com.ecommerce.domain.auth.ApiResponse;
import com.ecommerce.domain.exception.InvalidCredentialsException;
import com.ecommerce.domain.exception.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Value("${app.application-mode}")
    private String developmentMode;

    public final JWTService jwtService;
    public final AuthUseCase authUseCase;

    public AuthenticationController(JWTService jwtService, AuthUseCase authUseCase) {
        this.jwtService = jwtService;
        this.authUseCase = authUseCase;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> authenticate(@ModelAttribute SignupDTO signup,
                                          @AuthToken(name = "signup_token", message = "Your sign-up session has expired. Please start the sign-up process again.") String token) {
        if (!jwtService.isValid(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Your sign-up session has expired. Please start the sign-up process again."));
        }
        try {
            signup.setEmail(jwtService.getUserID(token));
            UserDTO user = authUseCase.signup(signup);

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole() != null ? user.getRole() : Role.CUSTOMER);

            String authToken = jwtService.generateToken(user.getEmail(), claims);

            ResponseCookie authCookie = ResponseCookie.from("AccessToken", authToken)
                    .httpOnly(true)
                    .secure(developmentMode.equalsIgnoreCase("PROD"))
                    .path("/")
                    .maxAge(86400 * 7) // 7 days
                    .sameSite("Lax")
                    .build();

            ApiResponse response = ApiResponse.success()
                    .add("message", "Signup successfully.")
                    .add("isNewUser", true)
                    .add("token", authToken)
                    .add("user", user);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                    .body(response);

        } catch (UserAlreadyExistException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Something went wrong. Try again."));
        }
    }

    @PutMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignupDTO signupDTO) {
        try {
            UserEntity user = authUseCase.login(signupDTO);

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole() != null ? user.getRole() : Role.CUSTOMER);

            String authToken = jwtService.generateToken(user.getEmail(), claims);

            ResponseCookie authCookie = ResponseCookie.from("AccessToken", authToken)
                    .httpOnly(true)
                    .secure(developmentMode.equalsIgnoreCase("PROD"))
                    .path("/")
                    .maxAge(86400 * 7) // 7 days
                    .sameSite("Lax")
                    .build();

            ApiResponse response = ApiResponse.success()
                    .add("message", "Logged in successfully.")
                    .add("token", authToken)
                    .add("user", UserDTO.fromEntity(user));

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                    .body(response);

        } catch (InvalidCredentialsException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Something went wrong. Try again."));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {

        ResponseCookie authCookie = ResponseCookie.from("AccessToken", "")
                .httpOnly(true)
                .secure(developmentMode.equalsIgnoreCase("PROD"))
                .path("/")
                .maxAge(0) // delete immediately
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                .body(ApiResponse.success()
                        .add("message", "Logged out successfully."));
    }
}
