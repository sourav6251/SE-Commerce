package com.ecommerce.application.port.in.user;

import com.ecommerce.adapter.in.web.auth.dto.UserDTO;

public interface UserUseCase {

    UserDTO update(UserDTO userDTO);
    void delete(String userID);
//    UserDTO create(UserDTO userDTO);
}
