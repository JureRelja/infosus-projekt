package com.example.demo.repositories;

import com.example.demo.models.Project;
import com.example.demo.models.ProjectMember;
import com.example.demo.models.ProjectStatus;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectMemberRepositoryTest {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private TestEntityManager em;

    private Project projectA;
    private Project projectB;
    private User userA;
    private User userB;
    private User userC;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("Member");
        em.persist(role);

        userA = newUser("a", role);
        userB = newUser("b", role);
        userC = newUser("c", role);
        em.persist(userA);
        em.persist(userB);
        em.persist(userC);

        ProjectStatus status = new ProjectStatus();
        status.setName("Aktivan");
        em.persist(status);

        projectA = newProject("PA", status, userA);
        projectB = newProject("PB", status, userA);
        em.persist(projectA);
        em.persist(projectB);

        em.flush();
    }

    private User newUser(String name, Role role) {
        User u = new User();
        u.setUsername(name);
        u.setPasswordHash("hash");
        u.setFirstName(name);
        u.setLastName("L");
        u.setEmail(name + "@example.com");
        u.setRole(role);
        u.setActive(true);
        return u;
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

    private void addMember(Project project, User user) {
        ProjectMember pm = new ProjectMember();
        pm.setProject(project);
        pm.setUser(user);
        em.persist(pm);
    }

    @Test
    void findByProjectId_returnsMembersOfThatProject() {
        addMember(projectA, userA);
        addMember(projectA, userB);
        addMember(projectB, userC);
        em.flush();

        List<ProjectMember> result = projectMemberRepository.findByProjectId(projectA.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(pm -> pm.getUser().getUsername())
                .containsExactlyInAnyOrder("a", "b");
    }

    @Test
    void deleteByProjectIdAndUserIdIn_removesOnlyMatchingMembers() {
        addMember(projectA, userA);
        addMember(projectA, userB);
        addMember(projectA, userC);
        addMember(projectB, userA);
        em.flush();

        projectMemberRepository.deleteByProjectIdAndUserIdIn(
                projectA.getId(),
                Set.of(userA.getId(), userB.getId())
        );
        em.flush();
        em.clear();

        List<ProjectMember> remaining = projectMemberRepository.findByProjectId(projectA.getId());
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getUser().getUsername()).isEqualTo("c");

        // userA's membership in projectB must be untouched
        List<ProjectMember> bMembers = projectMemberRepository.findByProjectId(projectB.getId());
        assertThat(bMembers).hasSize(1);
        assertThat(bMembers.get(0).getUser().getUsername()).isEqualTo("a");
    }

    @Test
    void deleteByProjectId_removesAllMembersOfThatProject() {
        addMember(projectA, userA);
        addMember(projectA, userB);
        addMember(projectB, userC);
        em.flush();

        projectMemberRepository.deleteByProjectId(projectA.getId());
        em.flush();
        em.clear();

        assertThat(projectMemberRepository.findByProjectId(projectA.getId())).isEmpty();
        assertThat(projectMemberRepository.findByProjectId(projectB.getId())).hasSize(1);
    }
}
