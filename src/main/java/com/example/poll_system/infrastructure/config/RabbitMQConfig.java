package com.example.poll_system.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    DirectExchange voteExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    Queue voteQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    Binding voteBinding(Queue voteQueue, DirectExchange voteExchange) {
        return BindingBuilder.bind(voteQueue).to(voteExchange).with(routingKey);
    }
}
