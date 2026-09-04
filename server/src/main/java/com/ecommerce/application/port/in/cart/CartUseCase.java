package com.ecommerce.application.port.in.cart;

import com.ecommerce.adapter.in.web.cart.dto.CartDTO;

public interface CartUseCase {

    CartDTO getCart(String userIdentifier);

    CartDTO addToCart(String userIdentifier, String productId, Integer quantity);

    CartDTO updateItemQuantity(String userIdentifier, String productId, Integer quantity);

    CartDTO removeItem(String userIdentifier, String productId);

    CartDTO clearCart(String userIdentifier);
}

