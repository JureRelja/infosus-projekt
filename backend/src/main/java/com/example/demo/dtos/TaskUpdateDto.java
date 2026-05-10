package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskUpdateDto(
        @NotBlank String name,
        String description,
        @NotNull Integer priorityId,
        @NotNull Integer statusId,
        @NotNull Integer projectId,
        Integer assigneeId,
        @NotNull Integer creatorId,
        LocalDate deadline
) {
}
