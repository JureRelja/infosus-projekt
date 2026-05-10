package com.example.demo.controllers;

import com.example.demo.dtos.TaskCreateDto;
import com.example.demo.dtos.TaskDto;
import com.example.demo.dtos.TaskUpdateDto;
import com.example.demo.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDto> findAll(@RequestParam(required = false) Integer projectId) {
        if (projectId != null) {
            return taskService.findByProjectId(projectId);
        }
        return taskService.findAll();
    }

    @GetMapping("/{id}")
    public TaskDto findById(@PathVariable Integer id) {
        return taskService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto create(@Valid @RequestBody TaskCreateDto dto) {
        return taskService.create(dto);
    }

    @PutMapping("/{id}")
    public TaskDto update(@PathVariable Integer id, @Valid @RequestBody TaskUpdateDto dto) {
        return taskService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        taskService.delete(id);
    }
}
