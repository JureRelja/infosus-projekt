package com.example.demo.services;

import com.example.demo.dtos.CommentCreateDto;
import com.example.demo.dtos.CommentDto;
import com.example.demo.dtos.CommentUpdateDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.CommentMapper;
import com.example.demo.models.Comment;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentService(
            CommentRepository commentRepository,
            TaskRepository taskRepository,
            UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<CommentDto> findAll() {
        return commentRepository.findAll().stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    public List<CommentDto> findByTaskId(Integer taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    public CommentDto findById(Integer id) {
        return CommentMapper.toDto(getEntityById(id));
    }

    @Transactional
    public CommentDto create(CommentCreateDto dto) {
        Task task = taskRepository.findById(dto.taskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + dto.taskId() + " not found"));
        User author = userRepository.findById(dto.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author (user) with id " + dto.authorId() + " not found"));

        Comment comment = new Comment();
        comment.setText(dto.text());
        comment.setTask(task);
        comment.setAuthor(author);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto update(Integer id, CommentUpdateDto dto) {
        Comment comment = getEntityById(id);
        comment.setText(dto.text());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public void delete(Integer id) {
        commentRepository.delete(getEntityById(id));
    }

    private Comment getEntityById(Integer id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id " + id + " not found"));
    }
}
