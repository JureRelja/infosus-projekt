package com.example.demo.controllers;

import com.example.demo.dtos.PriorityDto;
import com.example.demo.services.PriorityService;
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

@WebMvcTest(PriorityController.class)
class PriorityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PriorityService priorityService;

    @Test
    void findAll_returnsList() throws Exception {
        when(priorityService.findAll()).thenReturn(List.of(
                new PriorityDto(1, "Low", 1),
                new PriorityDto(2, "High", 2)
        ));

        mockMvc.perform(get("/priorities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Low"))
                .andExpect(jsonPath("$[1].orderIndex").value(2));
    }

    @Test
    void findAll_emptyListReturns200() throws Exception {
        when(priorityService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/priorities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
