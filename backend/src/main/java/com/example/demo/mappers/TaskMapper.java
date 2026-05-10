package com.example.demo.mappers;

import com.example.demo.dtos.TaskDto;
import com.example.demo.models.Task;

public final class TaskMapper {

    private TaskMapper() {
    }

    public static TaskDto toDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getPriority() != null ? task.getPriority().getId() : null,
                task.getStatus() != null ? task.getStatus().getId() : null,
                task.getProject() != null ? task.getProject().getId() : null,
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getCreator() != null ? task.getCreator().getId() : null,
                task.getDeadline(),
                task.getCreatedAt()
        );
    }
}
