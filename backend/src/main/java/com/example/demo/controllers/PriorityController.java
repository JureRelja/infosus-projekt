package com.example.demo.controllers;

import com.example.demo.dtos.PriorityDto;
import com.example.demo.services.PriorityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/priorities")
public class PriorityController {

    private final PriorityService priorityService;

    public PriorityController(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @GetMapping
    public List<PriorityDto> findAll() {
        return priorityService.findAll();
    }
}
