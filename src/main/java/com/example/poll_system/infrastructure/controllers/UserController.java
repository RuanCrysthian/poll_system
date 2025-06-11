package com.example.poll_system.infrastructure.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.poll_system.application.usecases.user.CreateUser;
import com.example.poll_system.application.usecases.user.FindUserByIdUseCase;
import com.example.poll_system.application.usecases.user.ListUserUseCase;
import com.example.poll_system.application.usecases.user.UpdateUserUseCase;
import com.example.poll_system.application.usecases.user.dtos.CreateUserInput;
import com.example.poll_system.application.usecases.user.dtos.CreateUserOutput;
import com.example.poll_system.application.usecases.user.dtos.FindUserByIdInput;
import com.example.poll_system.application.usecases.user.dtos.FindUserByIdOutput;
import com.example.poll_system.application.usecases.user.dtos.ListUserOutput;
import com.example.poll_system.application.usecases.user.dtos.UpdateUserInput;
import com.example.poll_system.application.usecases.user.dtos.UpdateUserOutput;
import com.example.poll_system.application.usecases.user.impl.CreateUserImpl;
import com.example.poll_system.application.usecases.user.impl.FindUserById;
import com.example.poll_system.application.usecases.user.impl.ListUserPageable;
import com.example.poll_system.application.usecases.user.impl.UpdateUser;
import com.example.poll_system.domain.gateways.UserRepository;
import com.example.poll_system.infrastructure.services.ObjectStorage;
import com.example.poll_system.infrastructure.services.PasswordEncoder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "API para gerenciamento de usuários do sistema de enquetes")
public class UserController {

    private final ObjectStorage objectStorage;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            ObjectStorage objectStorage,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.objectStorage = objectStorage;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema com os dados fornecidos e faz upload da imagem de perfil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateUserOutput.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos ou usuário já existe"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateUserOutput> createUser(
            @Parameter(description = "Dados do usuário a ser criado", required = true) @RequestPart("user") CreateUserInput input,
            @Parameter(description = "Arquivo de imagem para o perfil do usuário", required = true) @RequestPart("imageProfile") MultipartFile imageFile) {

        input.setImageProfile(imageFile);

        CreateUser createUserImpl = new CreateUserImpl(userRepository, passwordEncoder, objectStorage);
        CreateUserOutput output = createUserImpl.execute(input);

        return ResponseEntity.ok(output);
    }

    @GetMapping
    @Operation(summary = "Listar usuários com paginação", description = "Retorna uma lista paginada de todos os usuários do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários recuperada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<ListUserOutput>> list(
            @Parameter(description = "Parâmetros de paginação (page, size, sort)", example = "{\"page\": 0, \"size\": 10, \"sort\": \"name,asc\"}") Pageable pageable) {
        ListUserUseCase listUserUseCase = new ListUserPageable(userRepository);
        return ResponseEntity.ok(listUserUseCase.execute(pageable));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário específico baseado no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FindUserByIdOutput.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<FindUserByIdOutput> getUserById(
            @Parameter(description = "ID único do usuário", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable String userId) {
        FindUserByIdUseCase findUserByIdUseCase = new FindUserById(userRepository);
        return ResponseEntity.ok(findUserByIdUseCase.execute(new FindUserByIdInput(userId)));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente, incluindo a imagem de perfil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateUserOutput.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<UpdateUserOutput> update(
            @Parameter(description = "ID único do usuário a ser atualizado", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable String userId,
            @Parameter(description = "Novos dados do usuário", required = true, content = @Content(schema = @Schema(implementation = UpdateUserInput.class))) @RequestPart("user") UpdateUserInput body,
            @Parameter(description = "Nova imagem de perfil do usuário", required = true) @RequestPart("imageProfile") MultipartFile imageFile) {
        UpdateUserUseCase updateUserUseCase = new UpdateUser(userRepository, objectStorage);
        UpdateUserInput input = new UpdateUserInput(
                userId,
                body.name(),
                body.cpf(),
                body.email(),
                body.role(),
                imageFile);
        UpdateUserOutput output = updateUserUseCase.execute(input);
        return ResponseEntity.ok(output);
    }
}
