package com.ecommerce.adapter.in.web.auth;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
public class OTPController {

    @GetMapping("/send")
    public ResponseEntity<?> generateOTP(@RequestParam("email") String email) {

        return ResponseEntity.ok("success");
    }
}
