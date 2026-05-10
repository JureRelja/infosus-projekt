package com.example.demo.integration;

import com.example.demo.dtos.TaskCreateDto;
import com.example.demo.dtos.TaskUpdateDto;
import com.example.demo.models.Priority;
import com.example.demo.models.Project;
import com.example.demo.models.Task;
import com.example.demo.models.TaskStatus;
import com.example.demo.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskFlowIntegrationTest extends IntegrationTestBase {

    @Test
    void create_persistsTaskWithInitialStatus() throws Exception {
        User manager = persistUser("manager1");
        Project project = persistProject("P", manager);
        em.flush();

        TaskCreateDto dto = new TaskCreateDto(
                "Novi zadatak",
                "opis",
                PRIORITY_NORMAL_ID,
                project.getId(),
                null,
                manager.getId(),
                LocalDate.of(2026, 6, 1)
        );

        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Novi zadatak"))
                .andExpect(jsonPath("$.statusId").value(TASK_STATUS_OPEN_ID))
                .andExpect(jsonPath("$.projectId").value(project.getId()))
                .andReturn().getResponse().getContentAsString();

        Integer taskId = objectMapper.readTree(response).get("id").asInt();
        em.flush();
        em.clear();

        Task saved = em.find(Task.class, taskId);
        assertThat(saved).isNotNull();
        assertThat(saved.getStatus().getId()).isEqualTo(TASK_STATUS_OPEN_ID);
        assertThat(saved.getCreator().getId()).isEqualTo(manager.getId());
    }

    @Test
    void create_failsWhenAssigneeNotProjectMember() throws Exception {
        User manager = persistUser("manager1");
        User outsider = persistUser("outsider");
        Project project = persistProject("P", manager);
        em.flush();

        TaskCreateDto dto = new TaskCreateDto(
                "Z",
                null,
                PRIORITY_NORMAL_ID,
                project.getId(),
                outsider.getId(),
                manager.getId(),
                null
        );

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("nije član projekta")));
    }

    @Test
    void create_succeedsWhenAssigneeIsProjectMember() throws Exception {
        User manager = persistUser("manager1");
        User member = persistUser("member1");
        Project project = persistProject("P", manager);
        addMember(project, member);
        em.flush();

        TaskCreateDto dto = new TaskCreateDto(
                "Z",
                null,
                PRIORITY_NORMAL_ID,
                project.getId(),
                member.getId(),
                manager.getId(),
                null
        );

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assigneeId").value(member.getId()));
    }

    @Test
    void create_failsWhenDeadlineBeforeProjectStart() throws Exception {
        User manager = persistUser("manager1");
        Project project = persistProject("P", manager,
                STATUS_ACTIVE_ID,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 12, 31));
        em.flush();

        TaskCreateDto dto = new TaskCreateDto(
                "Z",
                null,
                PRIORITY_NORMAL_ID,
                project.getId(),
                null,
                manager.getId(),
                LocalDate.of(2026, 4, 1)
        );

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("Rok zadatka")));
    }

    @Test
    void findByProjectId_filtersCorrectly() throws Exception {
        User manager = persistUser("manager1");
        Project pA = persistProject("A", manager);
        Project pB = persistProject("B", manager);
        persistTask(pA, manager, "A1");
        persistTask(pA, manager, "A2");
        persistTask(pB, manager, "B1");
        em.flush();

        mockMvc.perform(get("/tasks").param("projectId", pA.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].projectId",
                        org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.is(pA.getId()))));
    }

    @Test
    void update_changesStatusAndPersists() throws Exception {
        User manager = persistUser("manager1");
        Project project = persistProject("P", manager);
        Task task = persistTask(project, manager, "T");
        em.flush();

        TaskUpdateDto dto = new TaskUpdateDto(
                "T-updated",
                "opis",
                PRIORITY_NORMAL_ID,
                TASK_STATUS_CLOSED_ID,
                project.getId(),
                null,
                manager.getId(),
                null
        );

        mockMvc.perform(put("/tasks/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("T-updated"))
                .andExpect(jsonPath("$.statusId").value(TASK_STATUS_CLOSED_ID));

        em.flush();
        em.clear();

        Task updated = em.find(Task.class, task.getId());
        assertThat(updated.getName()).isEqualTo("T-updated");
        assertThat(updated.getStatus().getId()).isEqualTo(TASK_STATUS_CLOSED_ID);
    }

    @Test
    void delete_removesTask() throws Exception {
        User manager = persistUser("manager1");
        Project project = persistProject("P", manager);
        Task task = persistTask(project, manager, "T");
        em.flush();
        Integer id = task.getId();

        mockMvc.perform(delete("/tasks/{id}", id))
                .andExpect(status().isNoContent());

        em.flush();
        em.clear();
        assertThat(em.find(Task.class, id)).isNull();
    }

    @Test
    void findById_returns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/tasks/{id}", 999_999))
                .andExpect(status().isNotFound());
    }

    private Task persistTask(Project project, User creator, String name) {
        Priority priority = em.find(Priority.class, PRIORITY_NORMAL_ID);
        TaskStatus statusOpen = em.find(TaskStatus.class, TASK_STATUS_OPEN_ID);
        Task t = new Task();
        t.setName(name);
        t.setProject(project);
        t.setCreator(creator);
        t.setPriority(priority);
        t.setStatus(statusOpen);
        em.persist(t);
        return t;
    }
}
