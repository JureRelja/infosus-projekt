package com.example.demo.services;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.ProjectStatus;
import com.example.demo.repositories.ProjectStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectStatusServiceTest {

    @Mock
    private ProjectStatusRepository projectStatusRepository;

    @InjectMocks
    private ProjectStatusService projectStatusService;

    private static ProjectStatus status(Integer id, String name) {
        ProjectStatus s = new ProjectStatus();
        s.setId(id);
        s.setName(name);
        return s;
    }

    @Test
    void findAll_returnsAllStatuses() {
        when(projectStatusRepository.findAll()).thenReturn(List.of(status(1, "Aktivan")));

        assertThat(projectStatusService.findAll()).hasSize(1);
    }

    @Test
    void findById_returnsStatus() {
        when(projectStatusRepository.findById(1)).thenReturn(Optional.of(status(1, "Aktivan")));

        assertThat(projectStatusService.findById(1).name()).isEqualTo("Aktivan");
    }

    @Test
    void findById_throwsNotFoundWhenMissing() {
        when(projectStatusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectStatusService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
