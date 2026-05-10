package com.example.demo.dtos;

import java.time.LocalDateTime;

public record StatusHistoryDto(
        Integer id,
        Integer taskId,
        Integer oldStatusId,
        Integer newStatusId,
        Integer changedById,
        String comment,
        LocalDateTime changedAt
) {
}
