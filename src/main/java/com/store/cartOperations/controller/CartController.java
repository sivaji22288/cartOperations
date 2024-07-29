package com.store.cartOperations.controller;

import com.store.cartOperations.pojo.AddItemRequest;
import com.store.cartOperations.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCart(@PathVariable Integer cartId) {
        try {
            return ResponseEntity.ok(cartService.getCart(cartId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{cartId}/item")
    public ResponseEntity<?> addItemToCart(
            @PathVariable Integer cartId, @RequestBody AddItemRequest productDetails) {
        try {
            return ResponseEntity.ok(cartService.addItem(cartId, productDetails.getProductId(), productDetails.getQuantity()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{cartId}/item/{itemId}")
    public ResponseEntity<?> updateItemQuantityToCart(
            @PathVariable Integer cartId, @PathVariable Integer itemId, @RequestParam @Valid @Min(1) Integer quantity) {
        try {
            return ResponseEntity.ok(cartService.updateItemQuantity(cartId, itemId, quantity));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{cartId}/item/{itemId}")
    public ResponseEntity<?> removeItemFromCart(
            @PathVariable Integer cartId, @PathVariable Integer itemId) {
        try {
            return ResponseEntity.ok(cartService.removeItem(cartId, itemId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}