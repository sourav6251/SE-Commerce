package com.ecommerce.adapter.in.web.user;

import com.ecommerce.adapter.in.web.auth.dto.UserDTO;
import com.ecommerce.annotation.CurrentUserId;
import com.ecommerce.application.port.in.user.UserUseCase;
import com.ecommerce.domain.auth.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PutMapping()
    public ResponseEntity<?> updateUser(@CurrentUserId String userId, @RequestBody UserDTO user) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User ID is required"));
        }
        user.setId(userId);
        try {
            userUseCase.update(user);
            return ResponseEntity.ok().body(ApiResponse.success()
                    .add("message", "User updated successfully.")
                    .add("user", user));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Something went wrong"));
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteUser(@CurrentUserId String userId) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User ID is required"));
        }
        try {
            userUseCase.delete(userId);
            return ResponseEntity.ok().body(ApiResponse.success().add("message", "User deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Something went wrong"));
        }
    }
}
