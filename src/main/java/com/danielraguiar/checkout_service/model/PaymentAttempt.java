package com.danielraguiar.checkout_service.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentAttempt {
    private LocalDateTime attemptedAt;
    private boolean successful;
    private String errorMessage;
    private String gatewayResponse;
}