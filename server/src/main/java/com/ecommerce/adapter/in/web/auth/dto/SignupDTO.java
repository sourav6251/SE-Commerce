package com.ecommerce.adapter.in.web.auth.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class SignupDTO {
    public String name;
    public String password;
    public String email;
    public String phone;
    public MultipartFile image;

}
