package com.ecommerce.adapter.in.web.auth.dto;


import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String role;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Converts UserEntity to UserDTO (omitting sensitive fields like password).
     */
    public static UserDTO fromEntity(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phoneNumber(entity.getPhoneNumber())
                .role(entity.getRole() != null ? entity.getRole().name() : null)
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}