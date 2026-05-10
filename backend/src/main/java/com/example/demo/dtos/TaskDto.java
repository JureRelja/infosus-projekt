package com.example.demo.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskDto(
        Integer id,
        String name,
        String description,
        Integer priorityId,
        Integer statusId,
        Integer projectId,
        Integer assigneeId,
        Integer creatorId,
        LocalDate deadline,
        LocalDateTime createdAt
) {
}
