package com.example.demo.mappers;

import com.example.demo.dtos.ChecklistItemDto;
import com.example.demo.models.ChecklistItem;

public final class ChecklistItemMapper {

    private ChecklistItemMapper() {
    }

    public static ChecklistItemDto toDto(ChecklistItem item) {
        return new ChecklistItemDto(
                item.getId(),
                item.getText(),
                item.getCompleted(),
                item.getTask() != null ? item.getTask().getId() : null,
                item.getCreatedAt()
        );
    }
}
