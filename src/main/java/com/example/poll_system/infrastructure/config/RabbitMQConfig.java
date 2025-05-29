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

    @Value("${app.rabbitmq.vote-queue}")
    private String voteQueueName;

    @Value("${app.rabbitmq.vote-routing-key}")
    private String voteRoutingKey;

    @Value("${app.rabbitmq.email-queue}")
    private String emailQueueName;

    @Value("${app.rabbitmq.email-routing-key}")
    private String emailRoutingKey;

    @Bean
    DirectExchange voteExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    Queue voteQueue() {
        return QueueBuilder.durable(voteQueueName).build();
    }

    @Bean
    Binding voteBinding(Queue voteQueue, DirectExchange voteExchange) {
        return BindingBuilder.bind(voteQueue).to(voteExchange).with(voteRoutingKey);
    }

    @Bean
    Queue emailQueue() {
        return QueueBuilder.durable(emailQueueName).build();
    }

    @Bean
    Binding emailBinding(Queue emailQueue, DirectExchange voteExchange) {
        return BindingBuilder.bind(emailQueue).to(voteExchange).with(emailRoutingKey);
    }
}
