package com.example.demo.services;

import com.example.demo.dtos.CommentCreateDto;
import com.example.demo.dtos.CommentDto;
import com.example.demo.dtos.CommentUpdateDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Comment;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private static User user(Integer id) {
        User u = new User();
        u.setId(id);
        return u;
    }

    private static Task task(Integer id) {
        Task t = new Task();
        t.setId(id);
        return t;
    }

    private static Comment comment(Integer id, String text, Task task, User author) {
        Comment c = new Comment();
        c.setId(id);
        c.setText(text);
        c.setTask(task);
        c.setAuthor(author);
        return c;
    }

    @Test
    void findAll_returnsAllComments() {
        when(commentRepository.findAll()).thenReturn(List.of(
                comment(1, "hi", task(1), user(10)),
                comment(2, "yo", task(1), user(11))
        ));

        assertThat(commentService.findAll()).hasSize(2);
    }

    @Test
    void findByTaskId_filtersByTask() {
        when(commentRepository.findByTaskId(1)).thenReturn(List.of(
                comment(1, "hi", task(1), user(10))
        ));

        List<CommentDto> result = commentService.findByTaskId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).taskId()).isEqualTo(1);
    }

    @Test
    void findById_returnsComment() {
        when(commentRepository.findById(1)).thenReturn(Optional.of(
                comment(1, "hi", task(1), user(10))
        ));

        CommentDto result = commentService.findById(1);

        assertThat(result.text()).isEqualTo("hi");
        assertThat(result.authorId()).isEqualTo(10);
    }

    @Test
    void findById_throwsNotFoundWhenMissing() {
        when(commentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_persistsComment() {
        CommentCreateDto dto = new CommentCreateDto("hello", 1, 10);
        when(taskRepository.findById(1)).thenReturn(Optional.of(task(1)));
        when(userRepository.findById(10)).thenReturn(Optional.of(user(10)));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        when(commentRepository.save(captor.capture())).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(99);
            return c;
        });

        CommentDto result = commentService.create(dto);

        assertThat(result.id()).isEqualTo(99);
        assertThat(captor.getValue().getText()).isEqualTo("hello");
        assertThat(captor.getValue().getTask().getId()).isEqualTo(1);
        assertThat(captor.getValue().getAuthor().getId()).isEqualTo(10);
    }

    @Test
    void create_throwsNotFoundWhenTaskMissing() {
        CommentCreateDto dto = new CommentCreateDto("x", 99, 10);
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void create_throwsNotFoundWhenAuthorMissing() {
        CommentCreateDto dto = new CommentCreateDto("x", 1, 99);
        when(taskRepository.findById(1)).thenReturn(Optional.of(task(1)));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_changesTextOnly() {
        Comment existing = comment(1, "old", task(1), user(10));
        when(commentRepository.findById(1)).thenReturn(Optional.of(existing));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        when(commentRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        CommentDto result = commentService.update(1, new CommentUpdateDto("new"));

        assertThat(result.text()).isEqualTo("new");
        Comment saved = captor.getValue();
        assertThat(saved.getTask().getId()).isEqualTo(1);
        assertThat(saved.getAuthor().getId()).isEqualTo(10);
    }

    @Test
    void update_throwsNotFoundWhenMissing() {
        when(commentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.update(99, new CommentUpdateDto("x")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_removesComment() {
        Comment c = comment(1, "x", task(1), user(10));
        when(commentRepository.findById(1)).thenReturn(Optional.of(c));

        commentService.delete(1);

        verify(commentRepository).delete(c);
    }

    @Test
    void delete_throwsNotFoundWhenMissing() {
        when(commentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
