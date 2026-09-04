package com.ecommerce.application.port.out.persistence;

import com.ecommerce.adapter.out.persistence.jpa.entity.AddressEntity;

import java.util.List;
import java.util.Optional;

public interface AddressPort {

    AddressEntity saveAddress(AddressEntity address);

    List<AddressEntity> saveAll(List<AddressEntity> addresses);

    Optional<AddressEntity> findById(String id);

    List<AddressEntity> findByUserIdentifier(String userIdentifier);

    Optional<AddressEntity> findByIdAndUserIdentifier(String id, String userIdentifier);

    List<AddressEntity> findDefaultsByUserIdentifier(String userIdentifier);

    void deleteAddress(String id);
}
