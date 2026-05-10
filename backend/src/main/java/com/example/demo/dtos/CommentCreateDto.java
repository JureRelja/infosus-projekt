package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateDto(
        @NotBlank String text,
        @NotNull Integer taskId,
        @NotNull Integer authorId
) {
}
