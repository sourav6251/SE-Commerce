package com.ecommerce.domain.exception;

public class UserAlreadyExist extends RuntimeException {
    public UserAlreadyExist(String message) {
        super(message);
    }
    public UserAlreadyExist() {
        super("User already exist");
    }
}
