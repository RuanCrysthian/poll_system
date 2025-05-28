package com.example.poll_system.domain.gateways;

public interface MessageQueueGateway {
    void send(String exchange, Object message, String routingKey);
}
