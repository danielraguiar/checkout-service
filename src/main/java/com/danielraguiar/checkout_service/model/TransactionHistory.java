package com.danielraguiar.checkout_service.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

@Data
@Document(collection = "transactions")
public class TransactionHistory {
    @Id
    private String id;
    private Long orderId;
    private String customerEmail;
    private BigDecimal amount;
    private String currentStatus;
    private LocalDateTime createdAt;
    private List<StatusChange> statusHistory;
    private List<PaymentAttempt> paymentAttempts;

    public TransactionHistory() {
        this.statusHistory = new ArrayList<>();
        this.paymentAttempts = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }
}