package com.example.demo.dtos;

import jakarta.validation.constraints.NotNull;

public record ProjectStatusPatchDto(@NotNull Integer statusId) {
}
