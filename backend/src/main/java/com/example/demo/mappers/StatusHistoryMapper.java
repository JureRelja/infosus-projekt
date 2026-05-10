package com.example.demo.mappers;

import com.example.demo.dtos.StatusHistoryDto;
import com.example.demo.models.StatusHistory;

public final class StatusHistoryMapper {

    private StatusHistoryMapper() {
    }

    public static StatusHistoryDto toDto(StatusHistory history) {
        return new StatusHistoryDto(
                history.getId(),
                history.getTask() != null ? history.getTask().getId() : null,
                history.getOldStatus() != null ? history.getOldStatus().getId() : null,
                history.getNewStatus() != null ? history.getNewStatus().getId() : null,
                history.getChangedBy() != null ? history.getChangedBy().getId() : null,
                history.getComment(),
                history.getChangedAt()
        );
    }
}
