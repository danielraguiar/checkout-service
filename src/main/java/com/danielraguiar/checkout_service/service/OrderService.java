package com.danielraguiar.checkout_service.service;

import com.danielraguiar.checkout_service.model.Order;
import com.danielraguiar.checkout_service.model.enums.OrderStatus;
import com.danielraguiar.checkout_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final TransactionHistoryService transactionHistoryService;

    private static final int PAYMENT_TIMEOUT_SECONDS = 30;

    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);

        log.info("Created order {} for customer {}", savedOrder.getId(), savedOrder.getCustomerEmail());

        transactionHistoryService.recordNewTransaction(savedOrder);

        rabbitTemplate.convertAndSend("payment_exchange", "payment_request", savedOrder);

        return savedOrder;
    }

    public Optional<Order> findOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public void handlePaymentResult(Long orderId, boolean success) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        String oldStatus = order.getStatus().toString();

        OrderStatus newStatus = success ? OrderStatus.COMPLETED : OrderStatus.CANCELLED;
        order.setStatus(newStatus);
        orderRepository.save(order);

        transactionHistoryService.recordStatusChange(
                order,
                oldStatus,
                success ? "Payment successful" : "Payment failed"
        );

        log.info("Order {} status updated to {} due to payment {}",
                orderId,
                newStatus,
                success ? "success" : "failure");
    }
}