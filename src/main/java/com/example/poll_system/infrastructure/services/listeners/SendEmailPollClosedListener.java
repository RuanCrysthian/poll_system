package com.example.poll_system.infrastructure.services.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.poll_system.application.usecases.poll.PollStatistics;
import com.example.poll_system.application.usecases.user.SendEmailUseCase;
import com.example.poll_system.application.usecases.vote.dto.SendEmailInput;
import com.example.poll_system.domain.entities.events.PollClosedEvent;
import com.example.poll_system.domain.exceptions.FailedToSendMessageToQueueException;

@Component
public class SendEmailPollClosedListener {

    private final SendEmailUseCase sendEmailVoteProcessed;
    private final PollStatistics pollStatistics;

    public SendEmailPollClosedListener(
            SendEmailUseCase sendEmailToUserUseCase,
            PollStatistics pollStatistics) {
        this.sendEmailVoteProcessed = sendEmailToUserUseCase;
        this.pollStatistics = pollStatistics;
    }

    @RabbitListener(queues = "${app.rabbitmq.email-poll-close.queue}")
    @Transactional(readOnly = true)
    public void listenPollClosedEvent(PollClosedEvent event) {
        try {
            String emailBody = buildPollClosedEmailBody(event.getPollId());
            SendEmailInput input = new SendEmailInput(
                    event.getOwnerEmail(),
                    "📊 Resultados da sua Enquete - Fechamento Automático",
                    emailBody);
            sendEmailVoteProcessed.execute(input);

        } catch (Exception e) {
            throw new FailedToSendMessageToQueueException(
                    "Erro ao enviar email de fechamento de enquete: " + e.getMessage());
        }
    }

    private String buildPollClosedEmailBody(String pollId) {
        try {
            var statisticsInput = new com.example.poll_system.application.usecases.poll.dto.PollStatisticsInput(pollId);
            var statistics = pollStatistics.getPollStatistics(statisticsInput);

            StringBuilder body = new StringBuilder();
            body.append("🎉 Sua enquete foi finalizada com sucesso!\n\n");
            body.append("📋 DETALHES DA ENQUETE:\n");
            body.append("• Título: ").append(statistics.pollTitle()).append("\n");
            body.append("• ID: ").append(statistics.pollId()).append("\n");
            body.append("• Status: ").append(statistics.pollStatus()).append("\n\n");

            body.append("📊 RESULTADOS FINAIS:\n");
            body.append("• Total de votos: ").append(statistics.totalVotes()).append("\n\n");

            if (statistics.totalVotes() > 0) {
                body.append("🏆 RANKING DAS OPÇÕES:\n");
                var sortedOptions = statistics.pollOptionsStatistics().stream()
                        .sorted((a, b) -> Long.compare(b.votesCount(), a.votesCount()))
                        .toList();

                for (int i = 0; i < sortedOptions.size(); i++) {
                    var option = sortedOptions.get(i);
                    String medal = getMedalEmoji(i);
                    double percentage = statistics.totalVotes() > 0
                            ? (double) option.votesCount() / statistics.totalVotes() * 100
                            : 0;

                    body.append(String.format("%s %s: %d votos (%.1f%%)\n",
                            medal, option.pollOptionDescription(), option.votesCount(), percentage));
                }
            } else {
                body.append("⚠️ Nenhum voto foi registrado para esta enquete.\n");
            }

            body.append("\n✅ A enquete foi fechada automaticamente e não aceita mais votos.");
            body.append("\n\nObrigado por usar nosso sistema de votação!");

            return body.toString();

        } catch (Exception e) {
            // Fallback para o body simples em caso de erro
            return "A enquete com ID " + pollId + " foi fechada com sucesso! " +
                    "Infelizmente não foi possível gerar as estatísticas detalhadas neste momento.";
        }
    }

    private String getMedalEmoji(int position) {
        return switch (position) {
            case 0 -> "🥇";
            case 1 -> "🥈";
            case 2 -> "🥉";
            default -> "🔸";
        };
    }
}
