package com.example.demo.mappers;

import com.example.demo.dtos.TaskStatusDto;
import com.example.demo.models.TaskStatus;

public final class TaskStatusMapper {

    private TaskStatusMapper() {
    }

    public static TaskStatusDto toDto(TaskStatus status) {
        return new TaskStatusDto(status.getId(), status.getName(), status.getOrderIndex());
    }
}
