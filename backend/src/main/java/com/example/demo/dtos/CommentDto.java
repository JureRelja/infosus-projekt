package com.example.demo.dtos;

import java.time.LocalDateTime;

public record CommentDto(
        Integer id,
        String text,
        Integer taskId,
        Integer authorId,
        LocalDateTime createdAt
) {
}
