package com.example.demo.controllers;

import com.example.demo.dtos.TaskStatusDto;
import com.example.demo.services.TaskStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/task-statuses")
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @GetMapping
    public List<TaskStatusDto> findAll() {
        return taskStatusService.findAll();
    }
}
