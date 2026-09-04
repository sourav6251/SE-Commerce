package com.ecommerce.adapter.in.web.address;

import com.ecommerce.adapter.in.web.address.dto.AddressDTO;
import com.ecommerce.annotation.CurrentUserId;
import com.ecommerce.application.port.in.address.AddressUseCase;
import com.ecommerce.domain.auth.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressUseCase addressUseCase;

    @PostMapping
    public ResponseEntity<?> createAddress(@RequestBody AddressDTO addressDTO, @CurrentUserId String userID) {
        try {
            AddressDTO created = addressUseCase.createAddress(userID, addressDTO);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Address created successfully.")
                    .add("address", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserAddresses(@CurrentUserId String userID) {
        try {
            List<AddressDTO> addresses = addressUseCase.getUserAddresses(userID);
            ApiResponse response = ApiResponse.success()
                    .add("addresses", addresses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable("id") String id, @CurrentUserId String userID) {
        try {
            AddressDTO address = addressUseCase.getAddressById(userID, id);
            ApiResponse response = ApiResponse.success()
                    .add("address", address);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable("id") String id, @RequestBody AddressDTO addressDTO, @CurrentUserId String userID) {
        try {
            AddressDTO updated = addressUseCase.updateAddress(userID, id, addressDTO);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Address updated successfully.")
                    .add("address", updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable("id") String id, @CurrentUserId String userID) {
        try {
            addressUseCase.deleteAddress(userID, id);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Address deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<?> setDefaultAddress(@PathVariable("id") String id, @CurrentUserId String userID) {
        try {
            AddressDTO updated = addressUseCase.setDefaultAddress(userID, id);
            ApiResponse response = ApiResponse.success()
                    .add("message", "Default address set successfully.")
                    .add("address", updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong"));
        }
    }
}
