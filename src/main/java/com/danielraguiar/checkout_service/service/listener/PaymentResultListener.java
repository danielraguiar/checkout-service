package com.danielraguiar.checkout_service.service.listener;

import com.danielraguiar.checkout_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResultListener {
    private final OrderService orderService;

    @RabbitListener(queues = "payment_result_queue")
    public void handlePaymentResult(PaymentResult result) {
        orderService.handlePaymentResult(result.getOrderId(), result.isSuccess());
    }
}
