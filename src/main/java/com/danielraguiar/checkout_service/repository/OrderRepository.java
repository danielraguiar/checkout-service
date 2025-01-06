package com.danielraguiar.checkout_service.repository;

import com.danielraguiar.checkout_service.model.Order;
import com.danielraguiar.checkout_service.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime time);
}
