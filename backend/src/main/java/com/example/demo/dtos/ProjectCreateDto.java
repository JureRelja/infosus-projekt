package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ProjectCreateDto(
        @NotBlank String name,
        String description,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull Integer statusId,
        @NotNull Integer managerId,
        List<Integer> memberIds
) {
}
