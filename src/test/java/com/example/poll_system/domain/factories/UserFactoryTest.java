package com.example.poll_system.domain.factories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.enums.UserRole;
import com.example.poll_system.domain.exceptions.BusinessRulesException;

public class UserFactoryTest {

    @Test
    void shouldCreateAdminUser() {
        String name = "Admin User";
        String cpf = "74571762097";
        String email = "admin@email.com";
        String password = "adminPassword";
        String role = "admin";
        String urlImageProfile = "http://example.com/admin.jpg";
        User user = UserFactory.create(name, cpf, email, password, role, urlImageProfile);
        Assertions.assertEquals(name, user.getName());
        Assertions.assertEquals(cpf, user.getCpf().getCpf());
        Assertions.assertEquals(email, user.getEmail().getEmail());
        Assertions.assertEquals(urlImageProfile, user.getUrlImageProfile());
        Assertions.assertTrue(user.isActive());
        Assertions.assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void shouldCreateVoterUser() {
        String name = "Voter User";
        String cpf = "74571762097";
        String email = "voter@email.com";
        String password = "voterPassword";
        String role = "voter";
        String urlImageProfile = "http://example.com/voter.jpg";
        User user = UserFactory.create(name, cpf, email, password, role, urlImageProfile);
        Assertions.assertEquals(name, user.getName());
        Assertions.assertEquals(cpf, user.getCpf().getCpf());
        Assertions.assertEquals(email, user.getEmail().getEmail());
        Assertions.assertEquals(urlImageProfile, user.getUrlImageProfile());
        Assertions.assertTrue(user.isActive());
        Assertions.assertEquals(UserRole.VOTER, user.getRole());
    }

    @Test
    void shouldThrowExceptionForInvalidRole() {
        String name = "Invalid Role User";
        String cpf = "74571762097";
        String email = "email@email.com";
        String password = "password";
        String role = "invalidRole";
        String urlImageProfile = "http://example.com/invalid.jpg";
        Assertions.assertThrows(BusinessRulesException.class, () -> {
            UserFactory.create(name, cpf, email, password, role, urlImageProfile);
        });
    }

    @Test
    void createUserFactory() {
        UserFactory userFactory = new UserFactory();
        Assertions.assertNotNull(userFactory);
    }
}
