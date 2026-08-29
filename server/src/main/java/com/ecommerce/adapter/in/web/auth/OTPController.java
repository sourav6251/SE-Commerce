package com.ecommerce.adapter.in.web.auth;

import com.ecommerce.adapter.in.web.auth.dto.UserDTO;
import com.ecommerce.adapter.out.persistence.enums.Role;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.UserRepository;
import com.ecommerce.annotation.AuthToken;
import com.ecommerce.application.port.in.otp.OtpUseCase;
import com.ecommerce.config.JWTService;
import com.ecommerce.domain.auth.ApiResponse;
import com.ecommerce.domain.exception.InvalidOtpException;
import com.ecommerce.domain.exception.OtpExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/otp")
public class OTPController {

    @Value("${app.application-mode}")
    private String developmentMode;

    private final OtpUseCase otpUseCase;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    public OTPController(OtpUseCase otpUseCase, JWTService jwtService, UserRepository userRepository) {
        this.otpUseCase = otpUseCase;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @GetMapping("/send")
    public ResponseEntity<?> generateOTP(@RequestParam("email") String email) {
        log.info("Send OTP email: {}", email);
        try {
            otpUseCase.generate(email);

            String token = jwtService.generateToken(email, 300000);

            ResponseCookie cookie = ResponseCookie.from("otp_token", token)
                    .httpOnly(true)
                    .secure(developmentMode.equalsIgnoreCase("PROD"))
                    .path("/")
                    .maxAge(300)
                    .sameSite("Lax")
                    .build();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "success");
            response.put("token", token);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("OTP generation failed");
        }
    }

    @PutMapping("/verify/signup")
    public ResponseEntity<?> verifySignupOTP(@AuthToken(name = "otp_token", message = "OTP Expired request again") String token,
                                             @RequestParam("otp") String otp) {

        try {
            if (!jwtService.isValid(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("OTP expired, please request again"));
            }

            String email = jwtService.getUserID(token);
            log.info("Verify OTP email: {}", email);

            // 1. Verify the OTP code
            otpUseCase.verify(email, otp);

            // 2. Check if user already exists in users table
            Optional<UserEntity> existingUser = userRepository.findByEmail(email);

            if (existingUser.isPresent()) {
                // User already exists -> Log in directly
                UserEntity user = existingUser.get();

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
                        .add("message", "User already exists. Logged in successfully.")
                        .add("isNewUser", false)
                        .add("token", authToken)
                        .add("user", UserDTO.fromEntity(user));

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                        .body(response);

            } else {
                // User does not exist -> Issue signup_token for completing registration
                String signUpToken = jwtService.generateToken(email, 1800000);

                ResponseCookie signupCookie = ResponseCookie.from("signup_token", signUpToken)
                        .httpOnly(true)
                        .secure(developmentMode.equalsIgnoreCase("PROD"))
                        .path("/")
                        .maxAge(1800)
                        .sameSite("Lax")
                        .build();

                ApiResponse response = ApiResponse.success()
                        .add("message", "OTP verified. Please complete signup.")
                        .add("isNewUser", true)
                        .add("token", signUpToken);

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, signupCookie.toString())
                        .body(response);
            }

        } catch (ExpiredJwtException | OtpExpiredException e) {
            log.warn("OTP expired: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("OTP expired, please request again"));
        } catch (InvalidOtpException | JwtException e) {
            log.warn("Invalid OTP or Token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyOTP(@AuthToken(name = "otp_token", message = "OTP Expired request again") String token, @RequestParam("otp") String otp) {
        log.info("Verify OTP token: {}", token);

        try {
            if (!jwtService.isValid(token)) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "OTP expired, please request again");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String email = jwtService.getUserID(token);
            log.info("Verify OTP email: {}", email);

            otpUseCase.verify(email, otp);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "success");
            return ResponseEntity.ok(response);

        } catch (ExpiredJwtException | OtpExpiredException e) {
            log.warn("OTP expired: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP expired, please request again");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (InvalidOtpException | JwtException e) {
            log.warn("Invalid OTP or Token: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
