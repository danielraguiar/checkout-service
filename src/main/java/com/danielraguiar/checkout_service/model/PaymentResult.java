package com.danielraguiar.checkout_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResult {
    private Long orderId;
    private boolean success;
    private String errorMessage;
    private String transactionId;
}
