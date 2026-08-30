package com.ecommerce.adapter.in.web.product;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//TODO: Working on product
@RestController
@RequestMapping("/product")
public class ProductController {

    @PostMapping()
    public ResponseEntity<?> addProduct(){

        return ResponseEntity.ok().build();
    }
}
