package com.danielraguiar.checkout_service.service.listener;

import com.danielraguiar.checkout_service.model.PaymentResult;
import com.danielraguiar.checkout_service.service.OrderService;
import com.danielraguiar.checkout_service.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultListener {
    private final OrderService orderService;
    private final TransactionHistoryService transactionHistoryService;

    @RabbitListener(queues = "payment_result_queue")
    public void handlePaymentResult(PaymentResult result) {
        log.info("Received payment result for order {}: success={}",
                result.getOrderId(),
                result.isSuccess());

        transactionHistoryService.recordPaymentAttempt(
                result.getOrderId(),
                result.isSuccess(),
                result.getTransactionId()
        );

        orderService.handlePaymentResult(
                result.getOrderId(),
                result.isSuccess()
        );
    }
}
