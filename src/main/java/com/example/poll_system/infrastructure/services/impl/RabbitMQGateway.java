package com.example.poll_system.infrastructure.services.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.poll_system.domain.gateways.MessageQueueGateway;

@Service
public class RabbitMQGateway implements MessageQueueGateway {
    private final RabbitTemplate rabbitTemplate;

    public RabbitMQGateway(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void send(String exchange, Object message, String routingKey) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

}
