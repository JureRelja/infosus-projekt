package com.example.demo.dtos;

public record UserDto(
        Integer id,
        String username,
        String firstName,
        String lastName,
        String email,
        Integer roleId
) {
}
