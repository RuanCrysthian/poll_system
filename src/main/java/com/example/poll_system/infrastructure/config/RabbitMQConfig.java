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

    @Value("${app.rabbitmq.vote-queue-dlq}")
    private String voteDeadLetterQueueName;

    @Value("${app.rabbitmq.vote-routing-key}")
    private String voteRoutingKey;

    @Value("${app.rabbitmq.vote-routing-key-dlq}")
    private String voteDeadLetterQueueRoutingKey;

    @Value("${app.rabbitmq.email-queue}")
    private String emailQueueName;

    @Value("${app.rabbitmq.email-queue-dlq}")
    private String emailDeadLetterQueueName;

    @Value("${app.rabbitmq.email-routing-key}")
    private String emailRoutingKey;

    @Value("${app.rabbitmq.email-poll-close.routing-key}")
    private String emailPollCloseRoutingKey;

    @Value("${app.rabbitmq.email-poll-close.queue}")
    private String emailPollCloseQueueName;

    @Value("${app.rabbitmq.email-routing-key-dlq}")
    private String emailDeadLetterQueueRoutingKey;

    @Value("${app.rabbitmq..email-poll-close.dlq}")
    private String emailPollCloseDeadLetterQueue;

    @Value("${app.rabbitmq..email-poll-close.dql-routing-key}")
    private String emailPollCloseDeadLetterQueueRoutingKey;

    @Bean
    DirectExchange voteExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    Queue voteQueue() {
        return QueueBuilder.durable(voteQueueName)
                .withArgument("x-dead-letter-exchange", exchangeName)
                .withArgument("x-dead-letter-routing-key", voteDeadLetterQueueRoutingKey)
                .build();
    }

    @Bean
    Binding voteBinding(Queue voteQueue, DirectExchange voteExchange) {
        return BindingBuilder.bind(voteQueue).to(voteExchange).with(voteRoutingKey);
    }

    @Bean
    Queue emailQueue() {
        return QueueBuilder.durable(emailQueueName)
                .withArgument("x-dead-letter-exchange", exchangeName)
                .withArgument("x-dead-letter-routing-key", emailDeadLetterQueueRoutingKey)
                .build();
    }

    @Bean
    Queue emailPollClosedQueue() {
        return QueueBuilder.durable(emailPollCloseQueueName)
                .withArgument("x-dead-letter-exchange", exchangeName)
                .withArgument("x-dead-letter-routing-key", emailPollCloseDeadLetterQueueRoutingKey)
                .build();
    }

    @Bean
    Binding emailBinding(Queue emailQueue, DirectExchange voteExchange) {
        return BindingBuilder.bind(emailQueue).to(voteExchange).with(emailRoutingKey);
    }

    @Bean
    Queue emailDLQ() {
        return QueueBuilder.durable(emailDeadLetterQueueName).build();
    }

    @Bean
    Queue emailPollClosedDLQ() {
        return QueueBuilder.durable(emailPollCloseDeadLetterQueue).build();
    }

    @Bean
    Binding emailDLQBinding(Queue emailDLQ, DirectExchange voteExchange) {
        return BindingBuilder.bind(emailDLQ).to(voteExchange).with(emailDeadLetterQueueRoutingKey);
    }

    @Bean
    Binding emailPollClosedDLQBinding(Queue emailPollClosedDLQ, DirectExchange voteExchange) {
        return BindingBuilder.bind(emailPollClosedDLQ).to(voteExchange).with(emailPollCloseDeadLetterQueueRoutingKey);
    }

    @Bean
    Binding emailPollCloseBinding(Queue emailPollClosedQueue, DirectExchange voteExchange) {
        return BindingBuilder.bind(emailPollClosedQueue).to(voteExchange).with(emailPollCloseRoutingKey);
    }

    @Bean
    Queue voteDLQ() {
        return QueueBuilder.durable(voteDeadLetterQueueName).build();
    }

    @Bean
    Binding voteDLQBinding(Queue voteDLQ, DirectExchange voteExchange) {
        return BindingBuilder.bind(voteDLQ).to(voteExchange).with(voteDeadLetterQueueRoutingKey);
    }
}
