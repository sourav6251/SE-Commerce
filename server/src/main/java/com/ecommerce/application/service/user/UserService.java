package com.ecommerce.application.service.user;

import com.ecommerce.adapter.in.web.auth.dto.UserDTO;
import com.ecommerce.application.port.in.user.UserUseCase;
import com.ecommerce.application.port.out.persistence.UserPort;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserUseCase {

    private final UserPort userPort;

    public UserService(UserPort userPort) {
        this.userPort = userPort;
    }

    @Override
    public UserDTO update(UserDTO userDTO) {
        return  userPort.updateUser(userDTO);
    }

    @Override
    public void delete(String userID) {
        userPort.deleteUser(userID);
    }
}
