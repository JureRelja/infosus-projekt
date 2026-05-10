package com.example.demo.integration;

import com.example.demo.dtos.ProjectCreateDto;
import com.example.demo.dtos.ProjectStatusPatchDto;
import com.example.demo.dtos.ProjectUpdateDto;
import com.example.demo.models.Priority;
import com.example.demo.models.Project;
import com.example.demo.models.ProjectMember;
import com.example.demo.models.Task;
import com.example.demo.models.TaskStatus;
import com.example.demo.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectFlowIntegrationTest extends IntegrationTestBase {

    @Test
    void create_persistsProjectAndMembers() throws Exception {
        User manager = persistUser("manager1");
        User member1 = persistUser("member1");
        User member2 = persistUser("member2");
        em.flush();

        ProjectCreateDto dto = new ProjectCreateDto(
                "Novi projekt",
                "opis",
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 11, 30),
                STATUS_ACTIVE_ID,
                manager.getId(),
                List.of(member1.getId(), member2.getId())
        );

        String response = mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Novi projekt"))
                .andExpect(jsonPath("$.managerId").value(manager.getId()))
                .andExpect(jsonPath("$.memberIds.length()").value(2))
                .andReturn().getResponse().getContentAsString();

        Integer projectId = objectMapper.readTree(response).get("id").asInt();
        em.flush();
        em.clear();

        Project saved = em.find(Project.class, projectId);
        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Novi projekt");

        List<ProjectMember> members = em.createQuery(
                        "select pm from ProjectMember pm where pm.project.id = :pid", ProjectMember.class)
                .setParameter("pid", projectId)
                .getResultList();
        assertThat(members).extracting(pm -> pm.getUser().getId())
                .containsExactlyInAnyOrder(member1.getId(), member2.getId());
    }

    @Test
    void create_returns400OnValidationError() throws Exception {
        String invalid = "{\"name\":\"\"}";

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_returnsCreatedProject() throws Exception {
        User manager = persistUser("manager1");
        Project project = persistProject("Postojeći", manager);
        em.flush();

        mockMvc.perform(get("/projects/{id}", project.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(project.getId()))
                .andExpect(jsonPath("$.name").value("Postojeći"))
                .andExpect(jsonPath("$.managerId").value(manager.getId()))
                .andExpect(jsonPath("$.statusId").value(STATUS_ACTIVE_ID));
    }

    @Test
    void findById_returns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/projects/{id}", 999_999))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_syncsMembers() throws Exception {
        User manager = persistUser("manager1");
        User m1 = persistUser("m1");
        User m2 = persistUser("m2");
        User m3 = persistUser("m3");
        Project project = persistProject("P", manager);
        addMember(project, m1);
        addMember(project, m2);
        em.flush();

        ProjectUpdateDto dto = new ProjectUpdateDto(
                "P-novo",
                "opis",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                STATUS_ACTIVE_ID,
                manager.getId(),
                List.of(m2.getId(), m3.getId())
        );

        mockMvc.perform(put("/projects/{id}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("P-novo"))
                .andExpect(jsonPath("$.memberIds.length()").value(2));

        em.flush();
        em.clear();

        List<ProjectMember> members = em.createQuery(
                        "select pm from ProjectMember pm where pm.project.id = :pid", ProjectMember.class)
                .setParameter("pid", project.getId())
                .getResultList();
        assertThat(members).extracting(pm -> pm.getUser().getId())
                .containsExactlyInAnyOrder(m2.getId(), m3.getId());
    }

    @Test
    void patchStatus_failsWhenOpenTasksExist() throws Exception {
        User manager = persistUser("manager1");
        Project project = persistProject("P", manager);
        persistTask(project, manager, TASK_STATUS_OPEN_ID);
        em.flush();

        ProjectStatusPatchDto dto = new ProjectStatusPatchDto(STATUS_COMPLETED_ID);

        mockMvc.perform(patch("/projects/{id}/status", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("nezavršenih zadataka")));

        em.flush();
        em.clear();

        Project after = em.find(Project.class, project.getId());
        assertThat(after.getStatus().getId()).isEqualTo(STATUS_ACTIVE_ID);
    }

    @Test
    void patchStatus_succeedsWhenAllTasksClosed() throws Exception {
        User manager = persistUser("manager1");
        Project project = persistProject("P", manager);
        persistTask(project, manager, TASK_STATUS_CLOSED_ID);
        em.flush();

        ProjectStatusPatchDto dto = new ProjectStatusPatchDto(STATUS_COMPLETED_ID);

        mockMvc.perform(patch("/projects/{id}/status", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusId").value(STATUS_COMPLETED_ID));

        em.flush();
        em.clear();

        Project after = em.find(Project.class, project.getId());
        assertThat(after.getStatus().getId()).isEqualTo(STATUS_COMPLETED_ID);
    }

    @Test
    void delete_removesProject() throws Exception {
        User manager = persistUser("manager1");
        Project project = persistProject("P", manager);
        em.flush();
        Integer id = project.getId();

        mockMvc.perform(delete("/projects/{id}", id))
                .andExpect(status().isNoContent());

        em.flush();
        em.clear();
        assertThat(em.find(Project.class, id)).isNull();
    }

    private Task persistTask(Project project, User creator, Integer statusId) {
        Priority priority = em.find(Priority.class, PRIORITY_NORMAL_ID);
        TaskStatus status = em.find(TaskStatus.class, statusId);
        Task t = new Task();
        t.setName("T");
        t.setProject(project);
        t.setCreator(creator);
        t.setPriority(priority);
        t.setStatus(status);
        em.persist(t);
        return t;
    }
}
