package com.example.demo.mappers;

import com.example.demo.dtos.CommentDto;
import com.example.demo.models.Comment;

public final class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getTask() != null ? comment.getTask().getId() : null,
                comment.getAuthor() != null ? comment.getAuthor().getId() : null,
                comment.getCreatedAt()
        );
    }
}
