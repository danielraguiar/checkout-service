package com.danielraguiar.checkout_service.controller;

import com.danielraguiar.checkout_service.model.Order;
import com.danielraguiar.checkout_service.model.enums.OrderStatus;
import com.danielraguiar.checkout_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        Order order = new Order();
        order.setCustomerEmail("test@example.com");
        order.setAmount(new BigDecimal("100.00"));
        order.setStatus(OrderStatus.PENDING);

        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerEmail").value("test@example.com"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrder_WhenExists_ShouldReturnOrder() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerEmail("test@example.com");
        order.setStatus(OrderStatus.COMPLETED);

        when(orderService.findOrder(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void getOrder_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(orderService.findOrder(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isNotFound());
    }
}