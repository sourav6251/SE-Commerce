package com.ecommerce.application.service.address;

import com.ecommerce.adapter.in.web.address.dto.AddressDTO;
import com.ecommerce.adapter.out.persistence.jpa.entity.AddressEntity;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;
import com.ecommerce.application.port.in.address.AddressUseCase;
import com.ecommerce.application.port.out.persistence.AddressPort;
import com.ecommerce.application.port.out.persistence.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService implements AddressUseCase {

    private final AddressPort addressPort;
    private final UserPort userPort;

    @Override
    @Transactional
    public AddressDTO createAddress(String userID, AddressDTO addressDTO) {
        if (addressDTO == null) {
            throw new IllegalArgumentException("Address data cannot be null");
        }

        // 1. Extract user credentials / entity using userID from @CurrentUserId
        UserEntity user = userPort.findUserEntityByIdOrEmail(userID);

        // 2. Fetch existing addresses for this user
        List<AddressEntity> existingAddresses = addressPort.findByUserIdentifier(user.getId());

        boolean shouldBeDefault = addressDTO.isDefault() || existingAddresses.isEmpty();

        if (shouldBeDefault && !existingAddresses.isEmpty()) {
            for (AddressEntity existing : existingAddresses) {
                if (existing.isDefault()) {
                    existing.setDefault(false);
                }
            }
            addressPort.saveAll(existingAddresses);
        }

        // 3. Save new address linked to the user entity
        AddressEntity newAddress = AddressEntity.builder()
                .user(user)
                .recipientName(addressDTO.getRecipientName())
                .phoneNumber(addressDTO.getPhoneNumber())
                .streetAddress(addressDTO.getStreetAddress())
                .city(addressDTO.getCity())
                .state(addressDTO.getState())
                .postalCode(addressDTO.getPostalCode())
                .country(addressDTO.getCountry() != null && !addressDTO.getCountry().isBlank() ? addressDTO.getCountry() : "India")
                .isDefault(shouldBeDefault)
                .build();

        AddressEntity savedAddress = addressPort.saveAddress(newAddress);
        return AddressDTO.fromEntity(savedAddress);
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(String userID, String addressId, AddressDTO addressDTO) {
        if (addressDTO == null) {
            throw new IllegalArgumentException("Address data cannot be null");
        }

        UserEntity user = userPort.findUserEntityByIdOrEmail(userID);

        AddressEntity existingAddress = addressPort.findByIdAndUserIdentifier(addressId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + addressId));

        if (addressDTO.getRecipientName() != null) {
            existingAddress.setRecipientName(addressDTO.getRecipientName());
        }
        if (addressDTO.getPhoneNumber() != null) {
            existingAddress.setPhoneNumber(addressDTO.getPhoneNumber());
        }
        if (addressDTO.getStreetAddress() != null) {
            existingAddress.setStreetAddress(addressDTO.getStreetAddress());
        }
        if (addressDTO.getCity() != null) {
            existingAddress.setCity(addressDTO.getCity());
        }
        if (addressDTO.getState() != null) {
            existingAddress.setState(addressDTO.getState());
        }
        if (addressDTO.getPostalCode() != null) {
            existingAddress.setPostalCode(addressDTO.getPostalCode());
        }
        if (addressDTO.getCountry() != null) {
            existingAddress.setCountry(addressDTO.getCountry());
        }

        if (addressDTO.isDefault() && !existingAddress.isDefault()) {
            List<AddressEntity> currentDefaults = addressPort.findDefaultsByUserIdentifier(user.getId());
            for (AddressEntity def : currentDefaults) {
                def.setDefault(false);
            }
            addressPort.saveAll(currentDefaults);
            existingAddress.setDefault(true);
        }

        AddressEntity updated = addressPort.saveAddress(existingAddress);
        return AddressDTO.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getUserAddresses(String userID) {
        UserEntity user = userPort.findUserEntityByIdOrEmail(userID);
        return addressPort.findByUserIdentifier(user.getId())
                .stream()
                .map(AddressDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getAddressById(String userID, String addressId) {
        UserEntity user = userPort.findUserEntityByIdOrEmail(userID);
        AddressEntity address = addressPort.findByIdAndUserIdentifier(addressId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + addressId));
        return AddressDTO.fromEntity(address);
    }

    @Override
    @Transactional
    public void deleteAddress(String userID, String addressId) {
        UserEntity user = userPort.findUserEntityByIdOrEmail(userID);
        AddressEntity address = addressPort.findByIdAndUserIdentifier(addressId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + addressId));

        boolean wasDefault = address.isDefault();
        addressPort.deleteAddress(address.getId());

        if (wasDefault) {
            List<AddressEntity> remaining = addressPort.findByUserIdentifier(user.getId());
            if (!remaining.isEmpty()) {
                AddressEntity newDefault = remaining.get(0);
                newDefault.setDefault(true);
                addressPort.saveAddress(newDefault);
            }
        }
    }

    @Override
    @Transactional
    public AddressDTO setDefaultAddress(String userID, String addressId) {
        UserEntity user = userPort.findUserEntityByIdOrEmail(userID);
        AddressEntity target = addressPort.findByIdAndUserIdentifier(addressId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + addressId));

        List<AddressEntity> currentDefaults = addressPort.findDefaultsByUserIdentifier(user.getId());
        for (AddressEntity def : currentDefaults) {
            def.setDefault(false);
        }
        addressPort.saveAll(currentDefaults);

        target.setDefault(true);
        AddressEntity saved = addressPort.saveAddress(target);
        return AddressDTO.fromEntity(saved);
    }
}
