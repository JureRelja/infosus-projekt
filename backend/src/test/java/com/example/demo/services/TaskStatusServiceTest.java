package com.example.demo.services;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.TaskStatus;
import com.example.demo.repositories.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskStatusServiceTest {

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @InjectMocks
    private TaskStatusService taskStatusService;

    private static TaskStatus status(Integer id, String name, Integer order) {
        TaskStatus s = new TaskStatus();
        s.setId(id);
        s.setName(name);
        s.setOrderIndex(order);
        return s;
    }

    @Test
    void findAll_callsSortedFinder() {
        when(taskStatusRepository.findAllByOrderByOrderIndexAsc())
                .thenReturn(List.of(status(1, "U pripremi", 1), status(2, "U postupku", 2)));

        assertThat(taskStatusService.findAll()).hasSize(2);
        verify(taskStatusRepository).findAllByOrderByOrderIndexAsc();
    }

    @Test
    void findById_returnsStatus() {
        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(status(1, "U pripremi", 1)));

        assertThat(taskStatusService.findById(1).orderIndex()).isEqualTo(1);
    }

    @Test
    void findById_throwsNotFoundWhenMissing() {
        when(taskStatusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskStatusService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
