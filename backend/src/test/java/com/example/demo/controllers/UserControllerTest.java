package com.example.demo.controllers;

import com.example.demo.dtos.UserDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private static UserDto userDto(int id) {
        return new UserDto(id, "user" + id, "First", "Last", "user" + id + "@example.com", 3);
    }

    @Test
    void findAll_returnsListOfUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userDto(1), userDto(2)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].roleId").value(3));
    }

    @Test
    void findAll_responseBodyDoesNotExposePasswordHash() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userDto(1)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].passwordHash").doesNotExist())
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void findById_returnsUser() throws Exception {
        when(userService.findById(eq(7))).thenReturn(userDto(7));

        mockMvc.perform(get("/users/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.username").value("user7"));
    }

    @Test
    void findById_returns404WhenMissing() throws Exception {
        when(userService.findById(eq(99))).thenThrow(new ResourceNotFoundException("User with id 99 not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 99 not found"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
