package com.example.demo.mappers;

import com.example.demo.dtos.ProjectDto;
import com.example.demo.models.Project;

import java.util.List;

public final class ProjectMapper {

    private ProjectMapper() {
    }

    public static ProjectDto toDto(Project project, List<Integer> memberIds) {
        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus() != null ? project.getStatus().getId() : null,
                project.getManager() != null ? project.getManager().getId() : null,
                memberIds != null ? memberIds : List.of(),
                project.getCreatedAt()
        );
    }
}
