package com.example.demo.controllers;

import com.example.demo.dtos.CommentCreateDto;
import com.example.demo.dtos.CommentDto;
import com.example.demo.dtos.CommentUpdateDto;
import com.example.demo.services.CommentService;
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
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDto> findAll(@RequestParam(required = false) Integer taskId) {
        if (taskId != null) {
            return commentService.findByTaskId(taskId);
        }
        return commentService.findAll();
    }

    @GetMapping("/{id}")
    public CommentDto findById(@PathVariable Integer id) {
        return commentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@Valid @RequestBody CommentCreateDto dto) {
        return commentService.create(dto);
    }

    @PutMapping("/{id}")
    public CommentDto update(@PathVariable Integer id, @Valid @RequestBody CommentUpdateDto dto) {
        return commentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        commentService.delete(id);
    }
}
