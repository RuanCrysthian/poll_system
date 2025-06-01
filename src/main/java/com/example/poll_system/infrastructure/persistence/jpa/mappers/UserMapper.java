package com.example.poll_system.infrastructure.persistence.jpa.mappers;

import org.springframework.stereotype.Component;

import com.example.poll_system.domain.entities.User;
import com.example.poll_system.domain.value_objects.Cpf;
import com.example.poll_system.domain.value_objects.Email;
import com.example.poll_system.infrastructure.persistence.jpa.entities.UserEntity;

@Component
public class UserMapper {

    public UserEntity toEntity(User domainUser) {
        if (domainUser == null) {
            return null;
        }

        return new UserEntity(
                domainUser.getId(),
                domainUser.getName(),
                domainUser.getCpf().getCpf(),
                domainUser.getEmail().getEmail(),
                domainUser.getPassword(),
                domainUser.getRole(),
                domainUser.getUrlImageProfile(),
                domainUser.isActive());
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        Cpf cpf = new Cpf(entity.getCpf());
        Email email = new Email(entity.getEmail());

        // Using the appropriate factory method based on the role
        if (entity.getRole().name().equals("ADMIN")) {
            return User.createAdmin(
                    entity.getId(),
                    entity.getName(),
                    cpf,
                    email,
                    entity.getPassword(),
                    entity.getUrlImageProfile());
        } else {
            return User.createVoter(
                    entity.getId(),
                    entity.getName(),
                    cpf,
                    email,
                    entity.getPassword(),
                    entity.getUrlImageProfile());
        }
    }
}
