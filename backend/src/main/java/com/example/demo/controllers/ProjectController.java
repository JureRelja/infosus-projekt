package com.example.demo.controllers;

import com.example.demo.dtos.ProjectCreateDto;
import com.example.demo.dtos.ProjectDescriptionPatchDto;
import com.example.demo.dtos.ProjectDto;
import com.example.demo.dtos.ProjectStatusPatchDto;
import com.example.demo.dtos.ProjectUpdateDto;
import com.example.demo.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectDto> findAll() {
        return projectService.findAll();
    }

    @GetMapping("/{id}")
    public ProjectDto findById(@PathVariable Integer id) {
        return projectService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@Valid @RequestBody ProjectCreateDto dto) {
        return projectService.create(dto);
    }

    @PutMapping("/{id}")
    public ProjectDto update(@PathVariable Integer id, @Valid @RequestBody ProjectUpdateDto dto) {
        return projectService.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    public ProjectDto patchStatus(@PathVariable Integer id, @Valid @RequestBody ProjectStatusPatchDto dto) {
        return projectService.patchStatus(id, dto);
    }

    @PatchMapping("/{id}/description")
    public ProjectDto patchDescription(@PathVariable Integer id, @Valid @RequestBody ProjectDescriptionPatchDto dto) {
        return projectService.patchDescription(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        projectService.delete(id);
    }
}
