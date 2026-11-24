package com.marketplace.cart.controller;

import com.marketplace.cart.model.AddCartItemRequest;
import com.marketplace.cart.model.CartItemResponse;
import com.marketplace.cart.model.CartPreviewResponse;
import com.marketplace.cart.model.CartResponse;
import com.marketplace.cart.model.SelectionRequest;
import com.marketplace.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addItem(
            Authentication authentication,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addItem(authentication.getName(), request.productId()));
    }

    @GetMapping
    public CartResponse getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    @PatchMapping("/items/{productId}/selection")
    public CartItemResponse updateSelection(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody SelectionRequest request
    ) {
        return cartService.updateSelection(authentication.getName(), productId, request.selected());
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(Authentication authentication, @PathVariable Long productId) {
        cartService.removeItem(authentication.getName(), productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/preview")
    public CartPreviewResponse getPreview(Authentication authentication) {
        return cartService.getPreview(authentication.getName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("code", "VALIDATION_ERROR", "message", "Request validation failed"));
    }
}
