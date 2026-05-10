package com.example.demo.services;

import com.example.demo.dtos.RoleDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.RoleMapper;
import com.example.demo.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDto> findAll() {
        return roleRepository.findAll().stream()
                .map(RoleMapper::toDto)
                .toList();
    }

    public RoleDto findById(Integer id) {
        return roleRepository.findById(id)
                .map(RoleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role with id " + id + " not found"));
    }
}
