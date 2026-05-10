package com.example.demo.mappers;

import com.example.demo.dtos.UserDto;
import com.example.demo.models.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().getId() : null
        );
    }
}
