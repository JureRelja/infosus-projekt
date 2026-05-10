package com.example.demo.repositories;

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
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager em;

    private Project projectA;
    private Project projectB;
    private Priority priority;
    private TaskStatus status;
    private User creator;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("Member");
        em.persist(role);

        creator = new User();
        creator.setUsername("creator");
        creator.setPasswordHash("hash");
        creator.setFirstName("F");
        creator.setLastName("L");
        creator.setEmail("c@example.com");
        creator.setRole(role);
        creator.setActive(true);
        em.persist(creator);

        ProjectStatus pStatus = new ProjectStatus();
        pStatus.setName("Aktivan");
        em.persist(pStatus);

        projectA = newProject("P-A", pStatus, creator);
        projectB = newProject("P-B", pStatus, creator);
        em.persist(projectA);
        em.persist(projectB);

        priority = new Priority();
        priority.setName("Normal");
        priority.setOrderIndex(1);
        em.persist(priority);

        status = new TaskStatus();
        status.setName("Otvoren");
        status.setOrderIndex(1);
        em.persist(status);

        em.flush();
    }

    private Project newProject(String name, ProjectStatus s, User manager) {
        Project p = new Project();
        p.setName(name);
        p.setStartDate(LocalDate.of(2026, 1, 1));
        p.setEndDate(LocalDate.of(2026, 12, 31));
        p.setStatus(s);
        p.setManager(manager);
        return p;
    }

    private Task newTask(String name, Project project) {
        Task t = new Task();
        t.setName(name);
        t.setPriority(priority);
        t.setStatus(status);
        t.setProject(project);
        t.setCreator(creator);
        return t;
    }

    @Test
    void findByProjectId_returnsOnlyMatchingProjectTasks() {
        em.persist(newTask("A1", projectA));
        em.persist(newTask("A2", projectA));
        em.persist(newTask("B1", projectB));
        em.flush();

        List<Task> aTasks = taskRepository.findByProjectId(projectA.getId());

        assertThat(aTasks).hasSize(2);
        assertThat(aTasks).extracting(Task::getName).containsExactlyInAnyOrder("A1", "A2");
    }

    @Test
    void findByProjectId_returnsEmptyWhenNoMatches() {
        em.persist(newTask("A1", projectA));
        em.flush();

        assertThat(taskRepository.findByProjectId(projectB.getId())).isEmpty();
    }

    @Test
    void findByProjectId_returnsEmptyForUnknownProject() {
        assertThat(taskRepository.findByProjectId(99999)).isEmpty();
    }

    @Test
    void save_assignsId() {
        Task t = newTask("X", projectA);

        Task saved = taskRepository.save(t);

        assertThat(saved.getId()).isNotNull();
    }
}
