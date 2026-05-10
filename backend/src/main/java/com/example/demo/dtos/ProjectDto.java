package com.example.demo.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectDto(
        Integer id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Integer statusId,
        Integer managerId,
        List<Integer> memberIds,
        LocalDateTime createdAt
) {
}
