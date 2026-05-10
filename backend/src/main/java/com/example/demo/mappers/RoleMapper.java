package com.example.demo.mappers;

import com.example.demo.dtos.RoleDto;
import com.example.demo.models.Role;

public final class RoleMapper {

    private RoleMapper() {
    }

    public static RoleDto toDto(Role role) {
        return new RoleDto(role.getId(), role.getName());
    }
}
