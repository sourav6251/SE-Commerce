package com.ecommerce.adapter.out.persistence.jpa.adapter;

import com.ecommerce.adapter.in.web.auth.dto.UserDTO;
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
//        return user;
    }

    @Override
    public Boolean isUserExistByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserDTO saveUser(UserEntity userDTO) {
        UserEntity user=userRepository.save(userDTO);
        return UserDTO.fromEntity(user);
    }


}
