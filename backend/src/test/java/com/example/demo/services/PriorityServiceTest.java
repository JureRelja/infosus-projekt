package com.example.demo.services;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Priority;
import com.example.demo.repositories.PriorityRepository;
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
class PriorityServiceTest {

    @Mock
    private PriorityRepository priorityRepository;

    @InjectMocks
    private PriorityService priorityService;

    private static Priority priority(Integer id, String name, Integer order) {
        Priority p = new Priority();
        p.setId(id);
        p.setName(name);
        p.setOrderIndex(order);
        return p;
    }

    @Test
    void findAll_callsSortedFinder() {
        when(priorityRepository.findAllByOrderByOrderIndexAsc())
                .thenReturn(List.of(priority(1, "Nizak", 1)));

        assertThat(priorityService.findAll()).hasSize(1);
        verify(priorityRepository).findAllByOrderByOrderIndexAsc();
    }

    @Test
    void findById_returnsPriority() {
        when(priorityRepository.findById(1)).thenReturn(Optional.of(priority(1, "Nizak", 1)));

        assertThat(priorityService.findById(1).name()).isEqualTo("Nizak");
    }

    @Test
    void findById_throwsNotFoundWhenMissing() {
        when(priorityRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> priorityService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
