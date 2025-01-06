package com.danielraguiar.checkout_service.service;

import com.danielraguiar.checkout_service.model.Order;
import com.danielraguiar.checkout_service.model.enums.OrderStatus;
import com.danielraguiar.checkout_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);

        rabbitTemplate.convertAndSend("payment_exchange", "payment_request", savedOrder);
        return savedOrder;
    }

    public void handlePaymentResult(Long orderId, boolean success) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(success ? OrderStatus.COMPLETED : OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}