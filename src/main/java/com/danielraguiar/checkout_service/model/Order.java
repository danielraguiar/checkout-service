package com.danielraguiar.checkout_service.model;

import com.danielraguiar.checkout_service.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerEmail;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
