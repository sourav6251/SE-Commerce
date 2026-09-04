package com.ecommerce.adapter.out.persistence.jpa.adapter;

import com.ecommerce.adapter.in.web.auth.dto.UserDTO;
import com.ecommerce.adapter.out.persistence.enums.Status;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;
import com.ecommerce.adapter.out.persistence.jpa.repository.UserRepository;
import com.ecommerce.application.port.out.persistence.UserPort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserPersistenceAdapter implements UserPort {

    private final UserRepository userRepository;

    public UserPersistenceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserEntity findUserEntityByIdOrEmail(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new UsernameNotFoundException("User identifier cannot be empty");
        }
        if (identifier.contains("@")) {
            return findUserByEmail(identifier);
        }
        return userRepository.findById(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id or email: " + identifier));
    }

    @Override
    public Boolean isUserExistByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserDTO updateUser(UserDTO user) {
        UserEntity userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + user.getId()));

        if (user.getFirstName() != null) {
            userEntity.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            userEntity.setLastName(user.getLastName());
        }
        if (user.getPhoneNumber() != null) {
            userEntity.setPhoneNumber(user.getPhoneNumber());
        }
//        if (user.getRole() != null) {
//            userEntity.setRole(Role.valueOf(user.getRole()));
//        }
        if (user.getStatus() != null) {
            userEntity.setStatus(Status.valueOf(user.getStatus()));
        }

        UserEntity updatedUser = userRepository.save(userEntity);
        return UserDTO.fromEntity(updatedUser);
    }

    @Override
    public UserEntity findUserById(String id) {
        return  userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
//        return UserDTO.fromEntity(userEntity);
    }

    @Override
    public void deleteUser(String userID) {
        userRepository.deleteById(userID);
    }

    @Override
    public UserDTO saveUser(UserEntity userDTO) {
        UserEntity user=userRepository.save(userDTO);
        return UserDTO.fromEntity(user);
    }


}
