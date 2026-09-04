package com.ecommerce.application.port.out.persistence;

import com.ecommerce.adapter.in.web.auth.dto.UserDTO;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;

public interface UserPort {

    UserEntity findUserByEmail(String email);
    UserEntity findUserEntityByIdOrEmail(String identifier);
    UserDTO saveUser(UserEntity userDTO);
    Boolean isUserExistByEmail(String email);
    UserDTO updateUser(UserDTO userDTO);
    UserEntity findUserById(String id);
    void deleteUser(String userID);
}
