package com.example.poll_system.application.usecases.poll.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.poll_system.application.usecases.poll.dto.CreatePollInput;
import com.example.poll_system.application.usecases.poll.dto.CreatePollOutput;
import com.example.poll_system.application.usecases.poll.dto.PollOptionInput;
import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.exceptions.BusinessRulesException;
import com.example.poll_system.domain.factories.UserFactory;
import com.example.poll_system.domain.gateways.PollOptionRepository;
import com.example.poll_system.domain.gateways.PollRepository;
import com.example.poll_system.domain.gateways.UserRepository;

public class CreatePollImplTest {

    @InjectMocks
    private CreatePollImpl createPollImpl;

    @Mock
    private PollRepository pollRepository;

    @Mock
    private PollOptionRepository pollOptionRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createUser() {
        return UserFactory.create(
                "John Doe",
                "05938337089",
                "john.doe@email.com",
                "QAZ123qaz*",
                "admin",
                "urlImageProfile");
    }

    private List<PollOptionInput> createPollOptions() {
        List<PollOptionInput> options = new ArrayList<>();
        options.add(new PollOptionInput("Option 1"));
        options.add(new PollOptionInput("Option 2"));
        return options;
    }

    @Test
    void shouldThrowExceptionWhenOwnerDoesNotExist() {
        Mockito.when(userRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(BusinessRulesException.class, () -> {
            List<PollOptionInput> options = createPollOptions();
            CreatePollInput input = new CreatePollInput(
                    "Poll Title",
                    "Poll Description",
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1),
                    "aa",
                    options);
            createPollImpl.execute(input);
        });
    }

    @Test
    void shouldCreatePollSuccessfully() {
        User user = createUser();
        Mockito.when(userRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(user));
        List<PollOptionInput> options = createPollOptions();
        CreatePollInput input = new CreatePollInput(
                "Poll Title",
                "Poll Description",
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusDays(1),
                user.getId(),
                options);
        CreatePollOutput output = createPollImpl.execute(input);
        Assertions.assertNotNull(output);
        Assertions.assertEquals(input.title(), output.title());
        Assertions.assertEquals(input.description(), output.description());
        Assertions.assertEquals(input.ownerId(), output.ownerId());
        Assertions.assertEquals(input.startDate(), output.startDate());
        Assertions.assertEquals(input.endDate(), output.endDate());
        Assertions.assertEquals(input.options().size(), output.options().size());
        for (int i = 0; i < input.options().size(); i++) {
            Assertions.assertEquals(input.options().get(i).description(), output.options().get(i).description());
        }
        Mockito.verify(pollRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(pollOptionRepository, Mockito.times(1)).saveAll(Mockito.anyList());
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyString());
    }

}
