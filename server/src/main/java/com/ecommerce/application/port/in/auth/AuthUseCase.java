package com.ecommerce.application.port.in.auth;


import com.ecommerce.adapter.in.web.auth.dto.SignupDTO;
import com.ecommerce.adapter.in.web.auth.dto.UserDTO;
import com.ecommerce.adapter.out.persistence.jpa.entity.UserEntity;

public interface AuthUseCase {

    UserDTO signup(SignupDTO signupDTO);

    UserEntity login(SignupDTO signupDTO);


}
