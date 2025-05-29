package com.example.poll_system.application.usecases.vote.impl;

import org.springframework.stereotype.Service;

import com.example.poll_system.application.usecases.vote.SendEmailVoteProcessedUseCase;
import com.example.poll_system.application.usecases.vote.dto.VoteProcessedEmailInput;
import com.example.poll_system.infrastructure.services.MailSender;

@Service
public class SendEmailVoteProcessed implements SendEmailVoteProcessedUseCase {

    private final MailSender mailSender;

    public SendEmailVoteProcessed(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void execute(VoteProcessedEmailInput input) {
        mailSender.send(input.email(), input.subject(), input.body());
    }

}
