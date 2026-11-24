package com.marketplace.cart.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void cartRequiresValidAuthentication() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/cart").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}
