package com.danielraguiar.checkout_service.controller;
import com.danielraguiar.checkout_service.model.Order;
import com.danielraguiar.checkout_service.model.enums.OrderStatus;
import com.danielraguiar.checkout_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        return orderService.findOrder(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<OrderStatus> getOrderStatus(@PathVariable Long orderId) {
        return orderService.findOrder(orderId)
                .map(order -> ResponseEntity.ok(order.getStatus()))
                .orElse(ResponseEntity.notFound().build());
    }
}
