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

@RestController
@RequestMapping("/api/v1/users")
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
    public ResponseEntity<CreateUserOutput> createUser(
            @RequestPart("user") CreateUserInput input,
            @RequestPart("imageProfile") MultipartFile imageFile) {

        input.setImageProfile(imageFile);

        CreateUser createUserImpl = new CreateUserImpl(userRepository, passwordEncoder, objectStorage);
        CreateUserOutput output = createUserImpl.execute(input);

        return ResponseEntity.ok(output);
    }

    @GetMapping
    public ResponseEntity<Page<ListUserOutput>> list(Pageable pageable) {
        ListUserUseCase listUserUseCase = new ListUserPageable(userRepository);
        return ResponseEntity.ok(listUserUseCase.execute(pageable));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<FindUserByIdOutput> getUserById(@PathVariable String userId) {
        FindUserByIdUseCase findUserByIdUseCase = new FindUserById(userRepository);
        return ResponseEntity.ok(findUserByIdUseCase.execute(new FindUserByIdInput(userId)));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UpdateUserOutput> update(
            @PathVariable String userId,
            @RequestPart("user") UpdateUserInput body,
            @RequestPart("imageProfile") MultipartFile imageFile) {
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
