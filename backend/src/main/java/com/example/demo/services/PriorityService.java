package com.example.demo.services;

import com.example.demo.dtos.PriorityDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.PriorityMapper;
import com.example.demo.repositories.PriorityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PriorityService {

    private final PriorityRepository priorityRepository;

    public PriorityService(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    public List<PriorityDto> findAll() {
        return priorityRepository.findAllByOrderByOrderIndexAsc().stream()
                .map(PriorityMapper::toDto)
                .toList();
    }

    public PriorityDto findById(Integer id) {
        return priorityRepository.findById(id)
                .map(PriorityMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Priority with id " + id + " not found"));
    }
}
