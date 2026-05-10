package com.example.demo.services;

import com.example.demo.dtos.ProjectStatusDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.ProjectStatusMapper;
import com.example.demo.repositories.ProjectStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProjectStatusService {

    private final ProjectStatusRepository projectStatusRepository;

    public ProjectStatusService(ProjectStatusRepository projectStatusRepository) {
        this.projectStatusRepository = projectStatusRepository;
    }

    public List<ProjectStatusDto> findAll() {
        return projectStatusRepository.findAll().stream()
                .map(ProjectStatusMapper::toDto)
                .toList();
    }

    public ProjectStatusDto findById(Integer id) {
        return projectStatusRepository.findById(id)
                .map(ProjectStatusMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectStatus with id " + id + " not found"));
    }
}
