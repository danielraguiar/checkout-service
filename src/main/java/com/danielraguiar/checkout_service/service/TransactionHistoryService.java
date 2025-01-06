package com.danielraguiar.checkout_service.service;

import com.danielraguiar.checkout_service.model.Order;
import com.danielraguiar.checkout_service.model.PaymentAttempt;
import com.danielraguiar.checkout_service.model.StatusChange;
import com.danielraguiar.checkout_service.model.TransactionHistory;
import com.danielraguiar.checkout_service.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionHistoryService {
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public void recordNewTransaction(Order order) {
        TransactionHistory history = new TransactionHistory();
        history.setOrderId(order.getId());
        history.setCustomerEmail(order.getCustomerEmail());
        history.setAmount(order.getAmount());
        history.setCurrentStatus(order.getStatus().toString());

        StatusChange initialStatus = new StatusChange();
        initialStatus.setToStatus(order.getStatus().toString());
        initialStatus.setChangedAt(LocalDateTime.now());
        initialStatus.setReason("Order created");

        history.getStatusHistory().add(initialStatus);
        transactionHistoryRepository.save(history);

        redisTemplate.opsForValue()
                .set("order:status:" + order.getId(),
                        order.getStatus().toString(),
                        Duration.ofHours(24));
    }

    public void recordPaymentAttempt(Long orderId, boolean successful, String gatewayResponse) {
        TransactionHistory history = transactionHistoryRepository
                .findByOrderId(orderId)
                .orElseThrow();

        PaymentAttempt attempt = new PaymentAttempt();
        attempt.setAttemptedAt(LocalDateTime.now());
        attempt.setSuccessful(successful);
        attempt.setGatewayResponse(gatewayResponse);

        history.getPaymentAttempts().add(attempt);
        transactionHistoryRepository.save(history);
    }

    public void recordStatusChange(Order order, String fromStatus, String reason) {
        TransactionHistory history = transactionHistoryRepository
                .findByOrderId(order.getId())
                .orElseThrow();

        StatusChange statusChange = new StatusChange();
        statusChange.setFromStatus(fromStatus);
        statusChange.setToStatus(order.getStatus().toString());
        statusChange.setChangedAt(LocalDateTime.now());
        statusChange.setReason(reason);

        history.getStatusHistory().add(statusChange);
        history.setCurrentStatus(order.getStatus().toString());
        transactionHistoryRepository.save(history);

        redisTemplate.opsForValue()
                .set("order:status:" + order.getId(),
                        order.getStatus().toString(),
                        Duration.ofHours(24));
    }
}
