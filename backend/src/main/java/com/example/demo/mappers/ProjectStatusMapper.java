package com.example.demo.mappers;

import com.example.demo.dtos.ProjectStatusDto;
import com.example.demo.models.ProjectStatus;

public final class ProjectStatusMapper {

    private ProjectStatusMapper() {
    }

    public static ProjectStatusDto toDto(ProjectStatus status) {
        return new ProjectStatusDto(status.getId(), status.getName());
    }
}
