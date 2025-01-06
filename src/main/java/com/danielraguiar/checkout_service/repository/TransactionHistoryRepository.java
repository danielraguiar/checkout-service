package com.danielraguiar.checkout_service.repository;

import com.danielraguiar.checkout_service.model.TransactionHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionHistoryRepository extends MongoRepository<TransactionHistory, String> {
    Optional<TransactionHistory> findByOrderId(Long orderId);
    List<TransactionHistory> findByCurrentStatus(String status);
    List<TransactionHistory> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}

