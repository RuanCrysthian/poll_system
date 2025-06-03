package com.example.poll_system.application.usecases.user.impl;

import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.user.SendEmailUseCase;
import com.example.poll_system.application.usecases.vote.dto.SendEmailInput;
import com.example.poll_system.infrastructure.services.MailSender;

@Service
public class SendEmailImpl implements SendEmailUseCase {

    private final MailSender mailSender;

    public SendEmailImpl(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void execute(SendEmailInput input) {
        mailSender.send(input.email(), input.subject(), input.body());
    }

}
