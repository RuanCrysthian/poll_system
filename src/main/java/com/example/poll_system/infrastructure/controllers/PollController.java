package com.example.poll_system.infrastructure.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.poll_system.application.usecases.poll.CreatePoll;
import com.example.poll_system.application.usecases.poll.FindPollByIdUseCase;
import com.example.poll_system.application.usecases.poll.ListPollUseCase;
import com.example.poll_system.application.usecases.poll.PollStatistics;
import com.example.poll_system.application.usecases.poll.dto.CreatePollInput;
import com.example.poll_system.application.usecases.poll.dto.CreatePollOutput;
import com.example.poll_system.application.usecases.poll.dto.FindPollByIdInput;
import com.example.poll_system.application.usecases.poll.dto.FindPollByIdOutput;
import com.example.poll_system.application.usecases.poll.dto.ListPollOutput;
import com.example.poll_system.application.usecases.poll.dto.PollStatisticsInput;
import com.example.poll_system.application.usecases.poll.dto.PollStatisticsOutput;
import com.example.poll_system.application.usecases.poll.impl.CreatePollImpl;
import com.example.poll_system.application.usecases.poll.impl.FindPollById;
import com.example.poll_system.application.usecases.poll.impl.ListPollPageable;
import com.example.poll_system.application.usecases.poll.impl.PollStatisticsImpl;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.domain.gateways.VoteRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/polls")
@Tag(name = "Polls", description = "API para gerenciamento de enquetes incluindo criação, listagem e obtenção de estatísticas")
public class PollController {

    private final UserRepository userRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;

    public PollController(
            UserRepository userRepository,
            PollOptionRepository pollOptionRepository,
            PollRepository pollRepository,
            VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
    }

    @PostMapping()
    @Operation(summary = "Criar uma nova enquete", description = "Cria uma nova enquete com os detalhes fornecidos incluindo pergunta, opções e informações do criador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enquete criada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatePollOutput.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário criador não encontrado", content = @Content)
    })
    public ResponseEntity<CreatePollOutput> createPoll(
            @Parameter(description = "Dados para criação da enquete", required = true) @RequestBody CreatePollInput input) {
        CreatePoll useCase = new CreatePollImpl(pollRepository, pollOptionRepository, userRepository);
        CreatePollOutput output = useCase.execute(input);
        return ResponseEntity.ok(output);
    }

    @GetMapping()
    @Operation(summary = "Listar todas as enquetes", description = "Recupera uma lista paginada de todas as enquetes do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enquetes recuperadas com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ListPollOutput>> list(
            @Parameter(description = "Parâmetros de paginação (página, tamanho, ordenação)", required = false) Pageable pageable) {
        ListPollUseCase listPollUseCase = new ListPollPageable(pollRepository);
        return ResponseEntity.ok(listPollUseCase.execute(pageable));
    }

    @GetMapping("/{pollId}")
    @Operation(summary = "Buscar enquete por ID", description = "Recupera informações detalhadas sobre uma enquete específica incluindo suas opções")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enquete encontrada e recuperada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindPollByIdOutput.class))),
            @ApiResponse(responseCode = "404", description = "Enquete não encontrada", content = @Content)
    })
    public ResponseEntity<FindPollByIdOutput> findPollById(
            @Parameter(description = "Identificador único da enquete", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable String pollId) {
        FindPollByIdUseCase findPollById = new FindPollById(pollRepository);
        return ResponseEntity.ok(findPollById.execute(new FindPollByIdInput(pollId)));
    }

    @GetMapping("/{pollId}/statistics")
    @Operation(summary = "Obter estatísticas da enquete", description = "Recupera estatísticas de votação para uma enquete específica incluindo contagem de votos e percentuais para cada opção")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas da enquete recuperadas com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PollStatisticsOutput.class))),
            @ApiResponse(responseCode = "404", description = "Enquete não encontrada", content = @Content)
    })
    public ResponseEntity<PollStatisticsOutput> getPollStatistics(
            @Parameter(description = "Identificador único da enquete", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable String pollId) {
        PollStatisticsInput input = new PollStatisticsInput(pollId);
        PollStatistics pollStatistics = new PollStatisticsImpl(pollRepository, voteRepository);
        PollStatisticsOutput output = pollStatistics.getPollStatistics(input);
        return ResponseEntity.ok(output);
    }
}
