package com.danielraguiar.checkout_service.repository;

import com.danielraguiar.checkout_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
