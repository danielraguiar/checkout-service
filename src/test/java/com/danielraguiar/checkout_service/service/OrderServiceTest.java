package com.danielraguiar.checkout_service.service;

import com.danielraguiar.checkout_service.model.Order;
import com.danielraguiar.checkout_service.model.enums.OrderStatus;
import com.danielraguiar.checkout_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private TransactionHistoryService transactionHistoryService;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerEmail("test@example.com");
        testOrder.setAmount(new BigDecimal("100.00"));
    }

    @Test
    void createOrder_ShouldSetStatusToPendingAndSave() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.createOrder(testOrder);

        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(orderRepository).save(testOrder);
        verify(rabbitTemplate).convertAndSend(eq("payment_exchange"), eq("payment_request"), any(Order.class));
        verify(transactionHistoryService).recordNewTransaction(any(Order.class));
    }

    @Test
    void handlePaymentResult_WhenSuccessful_ShouldCompleteOrder() {
        testOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.handlePaymentResult(1L, true);

        assertEquals(OrderStatus.COMPLETED, testOrder.getStatus());
        verify(orderRepository).save(testOrder);
        verify(transactionHistoryService).recordStatusChange(eq(testOrder), eq("PENDING"), eq("Payment successful"));
    }

    @Test
    void handlePaymentResult_WhenFailed_ShouldCancelOrder() {
        testOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.handlePaymentResult(1L, false);

        assertEquals(OrderStatus.CANCELLED, testOrder.getStatus());
        verify(orderRepository).save(testOrder);
        verify(transactionHistoryService).recordStatusChange(eq(testOrder), eq("PENDING"), eq("Payment failed"));
    }

    @Test
    void findOrder_ShouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Optional<Order> result = orderService.findOrder(1L);

        assertTrue(result.isPresent());
        assertEquals(testOrder, result.get());
    }
}

