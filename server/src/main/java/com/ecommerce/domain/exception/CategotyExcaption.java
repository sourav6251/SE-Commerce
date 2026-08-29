package com.ecommerce.domain.exception;

public class CategotyExcaption extends RuntimeException {
    public CategotyExcaption(String message) {
        super(message);
    }
    public CategotyExcaption(){
        super("A error occurred during creating a new category");
    }
}
