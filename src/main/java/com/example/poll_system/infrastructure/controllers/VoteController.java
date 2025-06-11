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

import com.example.poll_system.application.usecases.vote.CreateVote;
import com.example.poll_system.application.usecases.vote.FindVoteByIdUseCase;
import com.example.poll_system.application.usecases.vote.ListVoteUseCase;
import com.example.poll_system.application.usecases.vote.dto.CreateVoteInput;
import com.example.poll_system.application.usecases.vote.dto.FindVoteByIdInput;
import com.example.poll_system.application.usecases.vote.dto.FindVoteByIdOutput;
import com.example.poll_system.application.usecases.vote.dto.ListVoteOutput;
import com.example.poll_system.application.usecases.vote.impl.FindVoteById;
import com.example.poll_system.application.usecases.vote.impl.ListVotePageable;
import com.example.poll_system.application.usecases.vote.impl.SendVoteToQueue;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.domain.gateways.VoteRepository;
import com.example.poll_system.infrastructure.services.EventPublisher;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/votes")
@Tag(name = "Votes", description = "API para gerenciamento de votos no sistema de enquetes")
public class VoteController {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PollOptionRepository pollOptionRepository;
    private final EventPublisher eventPublisher;
    private final PollRepository pollRepository;

    public VoteController(
            VoteRepository voteRepository,
            UserRepository userRepository,
            PollOptionRepository pollOptionRepository,
            EventPublisher eventPublisher,
            PollRepository pollRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.eventPublisher = eventPublisher;
        this.pollRepository = pollRepository;
    }

    @PostMapping()
    @Operation(summary = "Criar novo voto", description = "Registra um voto em uma enquete. O voto é enviado para uma fila de processamento assíncrono para garantir performance e consistência")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Voto aceito para processamento"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos ou enquete fechada para votação"),
            @ApiResponse(responseCode = "404", description = "Usuário ou opção de enquete não encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> createVote(
            @Parameter(description = "Dados do voto a ser criado", required = true) @RequestBody CreateVoteInput input) {
        CreateVote createVote = new SendVoteToQueue(pollOptionRepository, userRepository, pollRepository,
                eventPublisher);
        createVote.execute(input);
        return ResponseEntity.accepted().build();
    }

    // @GetMapping()
    // public ResponseEntity<List<Vote>> list() {
    // return ResponseEntity.ok(voteRepository.findAll());
    // }

    @GetMapping()
    @Operation(summary = "Listar votos com paginação", description = "Retorna uma lista paginada de todos os votos do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de votos recuperada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<ListVoteOutput>> list(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)", example = "{\"page\": 0, \"size\": 10, \"sort\": \"createdAt,desc\"}") Pageable pageable) {
        ListVoteUseCase listVoteUseCase = new ListVotePageable(voteRepository);
        return ResponseEntity.ok(listVoteUseCase.execute(pageable));
    }

    @GetMapping("/{voteId}")
    @Operation(summary = "Buscar voto por ID", description = "Retorna os dados detalhados de um voto específico baseado no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voto encontrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindVoteByIdOutput.class))),
            @ApiResponse(responseCode = "404", description = "Voto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<FindVoteByIdOutput> findVoteById(
            @Parameter(description = "ID único do voto", required = true, example = "750e8400-e29b-41d4-a716-446655440002") @PathVariable String voteId) {
        FindVoteByIdUseCase findVoteByIdUseCase = new FindVoteById(voteRepository);
        return ResponseEntity.ok(findVoteByIdUseCase.execute(new FindVoteByIdInput(voteId)));
    }

}
