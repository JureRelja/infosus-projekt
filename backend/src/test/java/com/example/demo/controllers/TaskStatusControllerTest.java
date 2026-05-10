package com.example.demo.controllers;

import com.example.demo.dtos.TaskStatusDto;
import com.example.demo.services.TaskStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskStatusController.class)
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskStatusService taskStatusService;

    @Test
    void findAll_returnsListInOrder() throws Exception {
        when(taskStatusService.findAll()).thenReturn(List.of(
                new TaskStatusDto(1, "Otvoren", 1),
                new TaskStatusDto(2, "U radu", 2),
                new TaskStatusDto(3, "Zatvoren", 3)
        ));

        mockMvc.perform(get("/task-statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].orderIndex").value(1))
                .andExpect(jsonPath("$[2].name").value("Zatvoren"));
    }
}
