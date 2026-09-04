package com.ecommerce.adapter.out.persistence.jpa.adapter;

import com.ecommerce.adapter.out.persistence.jpa.entity.AddressEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.AddressRepository;
import com.ecommerce.application.port.out.persistence.AddressPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AddressPersistenceAdapter implements AddressPort {

    private final AddressRepository addressRepository;

    @Override
    public AddressEntity saveAddress(AddressEntity address) {
        return addressRepository.save(address);
    }

    @Override
    public List<AddressEntity> saveAll(List<AddressEntity> addresses) {
        return addressRepository.saveAll(addresses);
    }

    @Override
    public Optional<AddressEntity> findById(String id) {
        return addressRepository.findById(id);
    }

    @Override
    public List<AddressEntity> findByUserIdentifier(String userIdentifier) {
        return addressRepository.findByUserIdentifier(userIdentifier);
    }

    @Override
    public Optional<AddressEntity> findByIdAndUserIdentifier(String id, String userIdentifier) {
        return addressRepository.findByIdAndUserIdentifier(id, userIdentifier);
    }

    @Override
    public List<AddressEntity> findDefaultsByUserIdentifier(String userIdentifier) {
        return addressRepository.findDefaultsByUserIdentifier(userIdentifier);
    }

    @Override
    public void deleteAddress(String id) {
        addressRepository.deleteById(id);
    }
}
