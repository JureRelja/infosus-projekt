package com.example.demo.controllers;

import com.example.demo.dtos.TaskCreateDto;
import com.example.demo.dtos.TaskDto;
import com.example.demo.dtos.TaskUpdateDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.services.TaskService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private static TaskDto taskDto(int id) {
        return new TaskDto(id, "Task " + id, "desc", 2, 1, 1, 11, 10,
                LocalDate.of(2026, 6, 1), LocalDateTime.of(2026, 1, 1, 12, 0));
    }

    @Test
    void findAll_withoutProjectId_callsFindAll() throws Exception {
        when(taskService.findAll()).thenReturn(List.of(taskDto(1), taskDto(2)));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(taskService).findAll();
        verify(taskService, never()).findByProjectId(any());
    }

    @Test
    void findAll_withProjectId_callsFindByProjectId() throws Exception {
        when(taskService.findByProjectId(eq(5))).thenReturn(List.of(taskDto(7)));

        mockMvc.perform(get("/tasks").param("projectId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(7));

        verify(taskService).findByProjectId(5);
        verify(taskService, never()).findAll();
    }

    @Test
    void findById_returnsTask() throws Exception {
        when(taskService.findById(1)).thenReturn(taskDto(1));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.assigneeId").value(11))
                .andExpect(jsonPath("$.creatorId").value(10));
    }

    @Test
    void findById_returns404WhenMissing() throws Exception {
        when(taskService.findById(99)).thenThrow(new ResourceNotFoundException("Task with id 99 not found"));

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void create_returns201WithTask() throws Exception {
        TaskCreateDto in = new TaskCreateDto("New", "d", 2, 1, 11, 10, LocalDate.of(2026, 6, 1));
        when(taskService.create(any(TaskCreateDto.class))).thenReturn(taskDto(42));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void create_returns400OnValidationError() throws Exception {
        // missing required fields: name (blank), priorityId, projectId, creatorId
        String invalid = "{\"name\": \"\"}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(taskService, never()).create(any());
    }

    @Test
    void update_returnsUpdatedTask() throws Exception {
        TaskUpdateDto in = new TaskUpdateDto("New name", "d", 3, 4, 2, 21, 20, LocalDate.of(2026, 9, 1));
        when(taskService.update(eq(1), any(TaskUpdateDto.class))).thenReturn(taskDto(1));

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_returns404WhenMissing() throws Exception {
        TaskUpdateDto in = new TaskUpdateDto("N", null, 1, 1, 1, null, 10, null);
        when(taskService.update(eq(99), any(TaskUpdateDto.class)))
                .thenThrow(new ResourceNotFoundException("Task with id 99 not found"));

        mockMvc.perform(put("/tasks/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).delete(1);
    }

    @Test
    void delete_returns404WhenMissing() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Task with id 99 not found"))
                .when(taskService).delete(99);

        mockMvc.perform(delete("/tasks/99"))
                .andExpect(status().isNotFound());
    }
}
