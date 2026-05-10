package com.example.demo.dtos;

import java.time.LocalDateTime;

public record ProjectMemberDto(
        Integer id,
        Integer projectId,
        Integer userId,
        LocalDateTime joinedAt
) {
}
