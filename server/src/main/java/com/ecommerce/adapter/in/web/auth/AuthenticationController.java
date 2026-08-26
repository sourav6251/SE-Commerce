package com.ecommerce.adapter.in.web.auth;


import com.ecommerce.annotation.AuthToken;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {




    @PostMapping("/signup")
    public ResponseEntity<?> authenticate() {
        return ResponseEntity.ok('s');
    }
}
