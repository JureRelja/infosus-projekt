package com.example.demo.controllers;

import com.example.demo.dtos.ProjectStatusDto;
import com.example.demo.services.ProjectStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project-statuses")
public class ProjectStatusController {

    private final ProjectStatusService projectStatusService;

    public ProjectStatusController(ProjectStatusService projectStatusService) {
        this.projectStatusService = projectStatusService;
    }

    @GetMapping
    public List<ProjectStatusDto> findAll() {
        return projectStatusService.findAll();
    }
}
