package com.example.poll_system.infrastructure.services;

public interface MailSender {
    void send(String to, String subject, String body);
}
