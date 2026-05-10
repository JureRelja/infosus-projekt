package com.example.demo.services;

import com.example.demo.dtos.UserDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.UserMapper;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public UserDto findById(Integer id) {
        return UserMapper.toDto(getEntityById(id));
    }

    public User getEntityById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }
}
