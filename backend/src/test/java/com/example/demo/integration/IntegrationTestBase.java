package com.example.demo.integration;

import com.example.demo.models.Project;
import com.example.demo.models.ProjectMember;
import com.example.demo.models.ProjectStatus;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/integration-seed.sql")
public abstract class IntegrationTestBase {

    protected static final Integer ROLE_EMPLOYEE_ID = 3;
    protected static final Integer STATUS_ACTIVE_ID = 1;
    protected static final Integer STATUS_COMPLETED_ID = 2;
    protected static final Integer PRIORITY_NORMAL_ID = 2;
    protected static final Integer TASK_STATUS_OPEN_ID = 1;
    protected static final Integer TASK_STATUS_CLOSED_ID = 3;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected EntityManager em;

    protected User persistUser(String username) {
        Role role = em.find(Role.class, ROLE_EMPLOYEE_ID);
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash("hash");
        u.setFirstName("F");
        u.setLastName("L");
        u.setEmail(username + "@example.com");
        u.setRole(role);
        u.setActive(true);
        em.persist(u);
        return u;
    }

    protected Project persistProject(String name, User manager) {
        return persistProject(name, manager, STATUS_ACTIVE_ID,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
    }

    protected Project persistProject(String name, User manager, Integer statusId,
                                     LocalDate startDate, LocalDate endDate) {
        ProjectStatus status = em.find(ProjectStatus.class, statusId);
        Project p = new Project();
        p.setName(name);
        p.setStartDate(startDate);
        p.setEndDate(endDate);
        p.setStatus(status);
        p.setManager(manager);
        em.persist(p);
        return p;
    }

    protected void addMember(Project project, User user) {
        ProjectMember pm = new ProjectMember();
        pm.setProject(project);
        pm.setUser(user);
        em.persist(pm);
    }
}
