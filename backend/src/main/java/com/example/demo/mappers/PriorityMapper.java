package com.example.demo.mappers;

import com.example.demo.dtos.PriorityDto;
import com.example.demo.models.Priority;

public final class PriorityMapper {

    private PriorityMapper() {
    }

    public static PriorityDto toDto(Priority priority) {
        return new PriorityDto(priority.getId(), priority.getName(), priority.getOrderIndex());
    }
}
