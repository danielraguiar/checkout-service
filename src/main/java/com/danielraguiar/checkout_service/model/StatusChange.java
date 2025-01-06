package com.danielraguiar.checkout_service.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatusChange {
    private String fromStatus;
    private String toStatus;
    private LocalDateTime changedAt;
    private String reason;
}