package com.example.demo.controllers;

import com.example.demo.dtos.ProjectStatusDto;
import com.example.demo.services.ProjectStatusService;
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

@WebMvcTest(ProjectStatusController.class)
class ProjectStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectStatusService projectStatusService;

    @Test
    void findAll_returnsList() throws Exception {
        when(projectStatusService.findAll()).thenReturn(List.of(
                new ProjectStatusDto(1, "Aktivan"),
                new ProjectStatusDto(2, "Završen")
        ));

        mockMvc.perform(get("/project-statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].name").value("Završen"));
    }
}
