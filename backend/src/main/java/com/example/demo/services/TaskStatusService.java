package com.example.demo.services;

import com.example.demo.dtos.TaskStatusDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.TaskStatusMapper;
import com.example.demo.repositories.TaskStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    public List<TaskStatusDto> findAll() {
        return taskStatusRepository.findAllByOrderByOrderIndexAsc().stream()
                .map(TaskStatusMapper::toDto)
                .toList();
    }

    public TaskStatusDto findById(Integer id) {
        return taskStatusRepository.findById(id)
                .map(TaskStatusMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found"));
    }
}
