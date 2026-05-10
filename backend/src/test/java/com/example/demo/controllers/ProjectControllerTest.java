package com.example.demo.controllers;

import com.example.demo.dtos.ProjectCreateDto;
import com.example.demo.dtos.ProjectDescriptionPatchDto;
import com.example.demo.dtos.ProjectDto;
import com.example.demo.dtos.ProjectStatusPatchDto;
import com.example.demo.dtos.ProjectUpdateDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.services.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    private static ProjectDto projectDto(int id) {
        return new ProjectDto(id, "P" + id, "desc",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                1, 10, List.of(11, 12),
                LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    @Test
    void findAll_returnsList() throws Exception {
        when(projectService.findAll()).thenReturn(List.of(projectDto(1), projectDto(2)));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].memberIds.length()").value(2));
    }

    @Test
    void findById_returnsProject() throws Exception {
        when(projectService.findById(1)).thenReturn(projectDto(1));

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.managerId").value(10));
    }

    @Test
    void findById_returns404WhenMissing() throws Exception {
        when(projectService.findById(99))
                .thenThrow(new ResourceNotFoundException("Project with id 99 not found"));

        mockMvc.perform(get("/projects/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returns201() throws Exception {
        ProjectCreateDto in = new ProjectCreateDto(
                "New", "desc",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                1, 10, List.of(11, 12)
        );
        when(projectService.create(any(ProjectCreateDto.class))).thenReturn(projectDto(42));

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void create_returns400OnValidationError() throws Exception {
        // missing name (blank), startDate, endDate, statusId, managerId
        String invalid = "{\"name\": \"\"}";

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).create(any());
    }

    @Test
    void update_returns200() throws Exception {
        ProjectUpdateDto in = new ProjectUpdateDto(
                "P1", "d",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                1, 10, List.of(11)
        );
        when(projectService.update(eq(1), any(ProjectUpdateDto.class))).thenReturn(projectDto(1));

        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_returns400OnInvalidDateRange() throws Exception {
        ProjectUpdateDto in = new ProjectUpdateDto(
                "P1", "d",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 1, 1),
                1, 10, List.of()
        );
        when(projectService.update(eq(1), any(ProjectUpdateDto.class)))
                .thenThrow(new IllegalArgumentException("Datum završetka projekta ne može biti prije datuma početka"));

        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void patchStatus_returns200() throws Exception {
        ProjectStatusPatchDto in = new ProjectStatusPatchDto(2);
        when(projectService.patchStatus(eq(1), any(ProjectStatusPatchDto.class))).thenReturn(projectDto(1));

        mockMvc.perform(patch("/projects/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void patchStatus_returns400WhenStatusIdMissing() throws Exception {
        String invalid = "{}";

        mockMvc.perform(patch("/projects/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchStatus_returns400OnDomainRuleViolation() throws Exception {
        ProjectStatusPatchDto in = new ProjectStatusPatchDto(2);
        when(projectService.patchStatus(eq(1), any(ProjectStatusPatchDto.class)))
                .thenThrow(new IllegalArgumentException(
                        "Projekt se ne može označiti kao završen — postoji 2 nezavršenih zadataka."));

        mockMvc.perform(patch("/projects/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchDescription_returns200() throws Exception {
        ProjectDescriptionPatchDto in = new ProjectDescriptionPatchDto("new desc");
        when(projectService.patchDescription(eq(1), any(ProjectDescriptionPatchDto.class)))
                .thenReturn(projectDto(1));

        mockMvc.perform(patch("/projects/1/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/projects/1"))
                .andExpect(status().isNoContent());

        verify(projectService).delete(1);
    }

    @Test
    void delete_returns404WhenMissing() throws Exception {
        doThrow(new ResourceNotFoundException("Project with id 99 not found"))
                .when(projectService).delete(99);

        mockMvc.perform(delete("/projects/99"))
                .andExpect(status().isNotFound());
    }
}
