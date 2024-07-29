package com.store.cartOperations.controller;

import com.store.cartOperations.domain.Cart;
import com.store.cartOperations.domain.Item;
import com.store.cartOperations.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Test
    void addItemToCartWithValidProductAndCart() throws Exception {

        //Given
        given(cartService.addItem(1, 1, 1)).willReturn(Cart.builder()
                .id(1)
                .totalCost(100.0)
                        .items(List.of(Item.builder()
                                .id(1)
                                .quantity(1)
                                .price(100.0)
                                .build()))
                .build());

        //When //Then
        mockMvc.perform(post("/cart/1/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\": 1, \"quantity\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalCost").value(100.0))
                .andExpect(jsonPath("$.items[0].id").value(1));
    }

    @Test
    void addItemToCartWithInValidProductAndCart() throws Exception {
        // Given
        given(cartService.addItem(1,-1,1)).willThrow(new RuntimeException("Product not found"));

        // When // Then
        mockMvc.perform(post("/cart/1/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\": -1, \"quantity\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemQuantityToCart() throws Exception {

        //Given
        given(cartService.updateItemQuantity(1, 1,2)).willReturn(Cart.builder()
                .id(1)
                .totalCost(200.0)
                .items(List.of(Item.builder()
                        .id(1)
                        .quantity(2)
                        .price(200.0)
                        .build()))
                .build());

        //When //Then
        mockMvc.perform(put("/cart/1/item/1?quantity=2")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalCost").value(200.0))
                .andExpect(jsonPath("$.items[0].id").value(1));
    }

    @Test
    void updateItemQuantityToCartWithInvalidQuantity() throws Exception {

        //Given

        //When //Then
        mockMvc.perform(put("/cart/1/item/1?quantity=0")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemQuantityToInvalidItemInCart() throws Exception {
        // Given
        given(cartService.updateItemQuantity(1,200,100)).willThrow(new RuntimeException("Item not found"));

        // When // Then
        mockMvc.perform(put("/cart/1/item/200?quantity=100")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeItemFromCart() throws Exception {

        //Given
        given(cartService.removeItem(1, 1)).willReturn(Cart.builder()
                .id(1)
                .totalCost(0.0)
                .items(new ArrayList<>())
                .build());

        //When //Then
        mockMvc.perform(delete("/cart/1/item/1")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalCost").value(0.0))
                .andExpect(jsonPath("$.items.size()").value(0));
    }

    @Test
    void removeInvalidItemFromCart() throws Exception {
        // Given
        given(cartService.removeItem(1,100)).willThrow(new RuntimeException("Item not found"));

        // When // Then
        mockMvc.perform(delete("/cart/1/item/100")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCartWithValidCartId() throws Exception {
        // Given
        given(cartService.getCart(1)).willReturn(Cart.builder()
                .id(1)
                .totalCost(100.0)
                .items(List.of(Item.builder()
                        .id(1)
                        .quantity(1)
                        .price(100.0)
                        .build()))
                .build());

        // When // Then
        mockMvc.perform(get("/cart/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalCost").value(100.0))
                .andExpect(jsonPath("$.items[0].id").value(1));
    }

    @Test
    void getCartWithInvalidCartId() throws Exception {
        // Given
        given(cartService.getCart(100)).willThrow(new RuntimeException("Cart not found"));

        // When // Then
        mockMvc.perform(get("/cart/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}