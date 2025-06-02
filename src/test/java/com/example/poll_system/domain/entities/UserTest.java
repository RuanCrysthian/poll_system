package com.example.poll_system.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.poll_system.domain.enums.UserRole;
import com.example.poll_system.domain.exceptions.FieldIsRequiredException;
import com.example.poll_system.domain.exceptions.InvalidFieldException;
import com.example.poll_system.domain.value_objects.Cpf;
import com.example.poll_system.domain.value_objects.Email;

public class UserTest {

    @Test
    void shouldCreateAdminUser() {
        String id = "1";
        String name = "John Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("john.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        User user = User.createAdmin(id, name, cpf, email, password, urlImageProfile);

        assertEquals(UserRole.ADMIN, user.getRole());
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(cpf, user.getCpf());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(urlImageProfile, user.getUrlImageProfile());
        assertTrue(user.isActive());
    }

    @Test
    void shouldCreateVoterUser() {
        String id = "1";
        String name = "Jane Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("jane.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        User user = User.createVoter(id, name, cpf, email, password, urlImageProfile);

        assertEquals(UserRole.VOTER, user.getRole());
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(cpf, user.getCpf());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(urlImageProfile, user.getUrlImageProfile());
        assertTrue(user.isActive());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        String id = null;
        String name = "John Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("john.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        assertThrows(FieldIsRequiredException.class, () -> {
            User.createAdmin(id, name, cpf, email, password, urlImageProfile);
        });
    }

    @Test
    void shouldThrowExceptionWhenIdIsEmpty() {
        String id = "";
        String name = "John Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("john.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        assertThrows(FieldIsRequiredException.class, () -> {
            User.createAdmin(id, name, cpf, email, password, urlImageProfile);
        });
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        String id = "1";
        String name = null;
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("john.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        assertThrows(FieldIsRequiredException.class, () -> {
            User.createAdmin(id, name, cpf, email, password, urlImageProfile);
        });
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        String id = "1";
        String name = "";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("john.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        assertThrows(FieldIsRequiredException.class, () -> {
            User.createAdmin(id, name, cpf, email, password, urlImageProfile);
        });
    }

    @Test
    void shouldThrowExceptionWhenCpfIsNull() {
        String id = "1";
        String name = "John Doe";
        Cpf cpf = null;
        Email email = new Email("john.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        assertThrows(FieldIsRequiredException.class, () -> {
            User.createAdmin(id, name, cpf, email, password, urlImageProfile);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        String id = "1";
        String name = "John Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = null;
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        assertThrows(FieldIsRequiredException.class, () -> {
            User.createAdmin(id, name, cpf, email, password, urlImageProfile);
        });
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        String id = "1";
        String name = "John Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("john.doe@email.com");
        String password = null;
        String urlImageProfile = "http://example.com/image.jpg";

        assertThrows(FieldIsRequiredException.class, () -> {
            User.createAdmin(id, name, cpf, email, password, urlImageProfile);
        });
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsEmpty() {
        String id = "1";
        String name = "John Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("john.doe@email.com");
        String password = "";
        String urlImageProfile = "http://example.com/image.jpg";

        assertThrows(FieldIsRequiredException.class, () -> {
            User.createAdmin(id, name, cpf, email, password, urlImageProfile);
        });
    }

    @Test
    void shouldThrowExceptionWhenIdIsOnlySpaces() {
        String id = "   ";
        String name = "John Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("john.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        assertThrows(FieldIsRequiredException.class, () -> {
            User.createAdmin(id, name, cpf, email, password, urlImageProfile);
        });
    }

    @Test
    void shouldThrowExceptionWhenNameIsOnlySpaces() {
        String id = "1";
        String name = "   ";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("john.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        assertThrows(FieldIsRequiredException.class, () -> {
            User.createAdmin(id, name, cpf, email, password, urlImageProfile);
        });
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsOnlySpaces() {
        String id = "1";
        String name = "John Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("john.doe@email.com");
        String password = "   ";
        String urlImageProfile = "http://example.com/image.jpg";

        assertThrows(FieldIsRequiredException.class, () -> {
            User.createAdmin(id, name, cpf, email, password, urlImageProfile);
        });
    }

    @Test
    void shouldCreateVoterWithNullUrlImageProfile() {
        String id = "1";
        String name = "Jane Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("jane.doe@email.com");
        String password = "password123";
        String urlImageProfile = null;

        User user = User.createVoter(id, name, cpf, email, password, urlImageProfile);

        assertEquals(UserRole.VOTER, user.getRole());
        assertEquals(urlImageProfile, user.getUrlImageProfile());
        assertTrue(user.isActive());
    }

    @Test
    void shouldThrowExceptionWhenCpfIsInvalidFormat() {
        String invalidCpf = "123.456.789-10";

        assertThrows(InvalidFieldException.class, () -> {
            new Cpf(invalidCpf);
        });
    }

    @Test
    void shouldThrowExceptionWhenCpfHasOnlyZeros() {
        String invalidCpf = "00000000000";

        assertThrows(InvalidFieldException.class, () -> {
            new Cpf(invalidCpf);
        });
    }

    @Test
    void shouldThrowExceptionWhenCpfHasRepeatedDigits() {
        String invalidCpf = "11111111111";

        assertThrows(InvalidFieldException.class, () -> {
            new Cpf(invalidCpf);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        String invalidEmail = "";

        assertThrows(InvalidFieldException.class, () -> {
            new Email(invalidEmail);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailIsOnlySpaces() {
        String invalidEmail = "   ";

        assertThrows(InvalidFieldException.class, () -> {
            new Email(invalidEmail);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailHasInvalidDomain() {
        String invalidEmail = "test@";

        assertThrows(InvalidFieldException.class, () -> {
            new Email(invalidEmail);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmailHasNoAtSymbol() {
        String invalidEmail = "testexample.com";

        assertThrows(InvalidFieldException.class, () -> {
            new Email(invalidEmail);
        });
    }

    @Test
    void shouldReturnTrueWhenUserIsAdmin() {
        String id = "1";
        String name = "Jane Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("jane.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        User user = User.createAdmin(id, name, cpf, email, password, urlImageProfile);

        assertEquals(UserRole.ADMIN, user.getRole());
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(cpf, user.getCpf());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(urlImageProfile, user.getUrlImageProfile());
        assertTrue(user.isAdmin());
        assertFalse(user.isVoter());
    }

    @Test
    void shouldReturnTrueWhenUserIsVoter() {
        String id = "1";
        String name = "Jane Doe";
        Cpf cpf = new Cpf("74571762097");
        Email email = new Email("jane.doe@email.com");
        String password = "password123";
        String urlImageProfile = "http://example.com/image.jpg";

        User user = User.createVoter(id, name, cpf, email, password, urlImageProfile);

        assertEquals(UserRole.VOTER, user.getRole());
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(cpf, user.getCpf());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(urlImageProfile, user.getUrlImageProfile());
        assertTrue(user.isVoter());
        assertFalse(user.isAdmin());
    }

}
