package com.example.poll_system.application.usecases.vote.dto;

public record SendEmailInput(
                String email,
                String subject,
                String body) {

}
