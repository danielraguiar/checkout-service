package com.danielraguiar.checkout_service.service;

import com.danielraguiar.checkout_service.model.Order;
import com.danielraguiar.checkout_service.model.TransactionHistory;
import com.danielraguiar.checkout_service.model.enums.OrderStatus;
import com.danielraguiar.checkout_service.repository.TransactionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionHistoryServiceTest {

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TransactionHistoryService transactionHistoryService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerEmail("test@example.com");
        testOrder.setAmount(new BigDecimal("100.00"));
        testOrder.setStatus(OrderStatus.PENDING);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void recordNewTransaction_ShouldSaveTransactionAndUpdateCache() {
        when(transactionHistoryRepository.save(any(TransactionHistory.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        transactionHistoryService.recordNewTransaction(testOrder);

        verify(transactionHistoryRepository).save(any(TransactionHistory.class));
        verify(valueOperations).set(eq("order:status:1"), eq("PENDING"), any());
    }

    @Test
    void recordPaymentAttempt_ShouldUpdateTransactionHistory() {
        TransactionHistory history = new TransactionHistory();
        when(transactionHistoryRepository.findByOrderId(1L)).thenReturn(Optional.of(history));
        when(transactionHistoryRepository.save(any(TransactionHistory.class))).thenReturn(history);

        transactionHistoryService.recordPaymentAttempt(1L, true, "tx_123");

        verify(transactionHistoryRepository).save(any(TransactionHistory.class));
    }
}
