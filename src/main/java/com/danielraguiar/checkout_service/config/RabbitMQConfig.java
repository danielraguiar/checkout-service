package com.danielraguiar.checkout_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue paymentResultQueue() {
        return new Queue("payment_result_queue");
    }

    @Bean
    public Exchange paymentExchange() {
        return new DirectExchange("payment_exchange");
    }

    @Bean
    public Binding paymentResultBinding(Queue paymentResultQueue, Exchange paymentExchange) {
        return BindingBuilder
                .bind(paymentResultQueue)
                .to(paymentExchange)
                .with("payment_result")
                .noargs();
    }
}