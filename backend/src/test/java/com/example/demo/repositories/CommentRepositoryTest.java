package com.example.demo.repositories;

import com.example.demo.models.Comment;
import com.example.demo.models.Priority;
import com.example.demo.models.Project;
import com.example.demo.models.ProjectStatus;
import com.example.demo.models.Role;
import com.example.demo.models.Task;
import com.example.demo.models.TaskStatus;
import com.example.demo.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    private Task task1;
    private Task task2;
    private User author;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("Member");
        em.persist(role);

        author = new User();
        author.setUsername("author");
        author.setPasswordHash("hash");
        author.setFirstName("A");
        author.setLastName("B");
        author.setEmail("a@example.com");
        author.setRole(role);
        author.setActive(true);
        em.persist(author);

        ProjectStatus pStatus = new ProjectStatus();
        pStatus.setName("Aktivan");
        em.persist(pStatus);

        Project project = new Project();
        project.setName("P");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 12, 31));
        project.setStatus(pStatus);
        project.setManager(author);
        em.persist(project);

        Priority priority = new Priority();
        priority.setName("Normal");
        priority.setOrderIndex(1);
        em.persist(priority);

        TaskStatus tStatus = new TaskStatus();
        tStatus.setName("Otvoren");
        tStatus.setOrderIndex(1);
        em.persist(tStatus);

        task1 = newTask("T1", project, priority, tStatus, author);
        task2 = newTask("T2", project, priority, tStatus, author);
        em.persist(task1);
        em.persist(task2);
        em.flush();
    }

    private Task newTask(String name, Project p, Priority pri, TaskStatus st, User creator) {
        Task t = new Task();
        t.setName(name);
        t.setProject(p);
        t.setPriority(pri);
        t.setStatus(st);
        t.setCreator(creator);
        return t;
    }

    private Comment newComment(String text, Task task) {
        Comment c = new Comment();
        c.setText(text);
        c.setTask(task);
        c.setAuthor(author);
        return c;
    }

    @Test
    void findByTaskId_returnsCommentsOfThatTaskOnly() {
        em.persist(newComment("hello", task1));
        em.persist(newComment("world", task1));
        em.persist(newComment("other", task2));
        em.flush();

        List<Comment> result = commentRepository.findByTaskId(task1.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Comment::getText)
                .containsExactlyInAnyOrder("hello", "world");
    }

    @Test
    void findByTaskId_returnsEmptyForUnknownTask() {
        assertThat(commentRepository.findByTaskId(99999)).isEmpty();
    }

    @Test
    void save_persistsAndAssignsId() {
        Comment c = newComment("text", task1);

        Comment saved = commentRepository.save(c);

        assertThat(saved.getId()).isNotNull();
        assertThat(commentRepository.findById(saved.getId())).isPresent();
    }
}
