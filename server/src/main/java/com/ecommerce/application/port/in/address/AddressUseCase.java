package com.ecommerce.application.port.in.address;

import com.ecommerce.adapter.in.web.address.dto.AddressDTO;

import java.util.List;

public interface AddressUseCase {

    AddressDTO createAddress(String userID, AddressDTO addressDTO);

    AddressDTO updateAddress(String userID, String addressId, AddressDTO addressDTO);

    List<AddressDTO> getUserAddresses(String userID);

    AddressDTO getAddressById(String userID, String addressId);

    void deleteAddress(String userID, String addressId);

    AddressDTO setDefaultAddress(String userID, String addressId);
}

