package com.example.demo.dtos;

import java.time.LocalDateTime;

public record ChecklistItemDto(
        Integer id,
        String text,
        Boolean completed,
        Integer taskId,
        LocalDateTime createdAt
) {
}
