package com.example.demo.controllers;

import com.example.demo.dtos.CommentCreateDto;
import com.example.demo.dtos.CommentDto;
import com.example.demo.dtos.CommentUpdateDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.services.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    private static CommentDto commentDto(int id) {
        return new CommentDto(id, "text " + id, 1, 10, LocalDateTime.of(2026, 1, 1, 9, 0));
    }

    @Test
    void findAll_withoutTaskId_callsFindAll() throws Exception {
        when(commentService.findAll()).thenReturn(List.of(commentDto(1), commentDto(2)));

        mockMvc.perform(get("/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(commentService).findAll();
        verify(commentService, never()).findByTaskId(any());
    }

    @Test
    void findAll_withTaskId_callsFindByTaskId() throws Exception {
        when(commentService.findByTaskId(eq(5))).thenReturn(List.of(commentDto(7)));

        mockMvc.perform(get("/comments").param("taskId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7));

        verify(commentService).findByTaskId(5);
    }

    @Test
    void findById_returnsComment() throws Exception {
        when(commentService.findById(1)).thenReturn(commentDto(1));

        mockMvc.perform(get("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("text 1"));
    }

    @Test
    void findById_returns404WhenMissing() throws Exception {
        when(commentService.findById(99))
                .thenThrow(new ResourceNotFoundException("Comment with id 99 not found"));

        mockMvc.perform(get("/comments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returns201() throws Exception {
        CommentCreateDto in = new CommentCreateDto("hello", 1, 10);
        when(commentService.create(any(CommentCreateDto.class))).thenReturn(commentDto(42));

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void create_returns400OnBlankText() throws Exception {
        CommentCreateDto in = new CommentCreateDto("", 1, 10);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).create(any());
    }

    @Test
    void create_returns400OnMissingTaskId() throws Exception {
        // taskId is @NotNull
        String invalid = "{\"text\": \"hi\", \"authorId\": 10}";

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_returnsUpdated() throws Exception {
        CommentUpdateDto in = new CommentUpdateDto("updated");
        when(commentService.update(eq(1), any(CommentUpdateDto.class))).thenReturn(commentDto(1));

        mockMvc.perform(put("/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_returns400OnBlankText() throws Exception {
        CommentUpdateDto in = new CommentUpdateDto("");

        mockMvc.perform(put("/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/comments/1"))
                .andExpect(status().isNoContent());

        verify(commentService).delete(1);
    }
}
