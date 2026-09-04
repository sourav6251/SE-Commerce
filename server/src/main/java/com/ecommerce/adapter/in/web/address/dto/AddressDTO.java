package com.ecommerce.adapter.in.web.address.dto;

import com.ecommerce.adapter.out.persistence.jpa.entity.AddressEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private String id;
    private String userId;
    private String recipientName;
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean isDefault;
    private Instant createdAt;
    private Instant updatedAt;

    public static AddressDTO fromEntity(AddressEntity entity) {
        if (entity == null) {
            return null;
        }
        return AddressDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .recipientName(entity.getRecipientName())
                .phoneNumber(entity.getPhoneNumber())
                .streetAddress(entity.getStreetAddress())
                .city(entity.getCity())
                .state(entity.getState())
                .postalCode(entity.getPostalCode())
                .country(entity.getCountry())
                .isDefault(entity.isDefault())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

