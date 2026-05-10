package com.example.demo.services;

import com.example.demo.dtos.TaskCreateDto;
import com.example.demo.dtos.TaskDto;
import com.example.demo.dtos.TaskUpdateDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Priority;
import com.example.demo.models.Project;
import com.example.demo.models.Task;
import com.example.demo.models.TaskStatus;
import com.example.demo.models.User;
import com.example.demo.repositories.PriorityRepository;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.TaskStatusRepository;
import com.example.demo.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private PriorityRepository priorityRepository;
    @Mock private TaskStatusRepository taskStatusRepository;

    @InjectMocks
    private TaskService taskService;

    private static User user(Integer id) {
        User u = new User();
        u.setId(id);
        return u;
    }

    private static Project project(Integer id) {
        Project p = new Project();
        p.setId(id);
        return p;
    }

    private static Priority priority(Integer id) {
        Priority p = new Priority();
        p.setId(id);
        p.setName("P" + id);
        p.setOrderIndex(id);
        return p;
    }

    private static TaskStatus status(Integer id) {
        TaskStatus s = new TaskStatus();
        s.setId(id);
        s.setName("S" + id);
        s.setOrderIndex(id);
        return s;
    }

    private static Task task(Integer id, Project pr, Priority pri, TaskStatus st, User creator, User assignee) {
        Task t = new Task();
        t.setId(id);
        t.setName("T" + id);
        t.setDescription("d");
        t.setProject(pr);
        t.setPriority(pri);
        t.setStatus(st);
        t.setCreator(creator);
        t.setAssignee(assignee);
        return t;
    }

    @Test
    void findAll_returnsAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(
                task(1, project(1), priority(1), status(1), user(10), null),
                task(2, project(1), priority(2), status(2), user(10), user(11))
        ));

        assertThat(taskService.findAll()).hasSize(2);
    }

    @Test
    void findByProjectId_filtersByProject() {
        when(taskRepository.findByProjectId(1)).thenReturn(List.of(
                task(1, project(1), priority(1), status(1), user(10), null)
        ));

        List<TaskDto> result = taskService.findByProjectId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).projectId()).isEqualTo(1);
    }

    @Test
    void findById_returnsTask() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(
                task(1, project(1), priority(1), status(1), user(10), user(11))
        ));

        TaskDto result = taskService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.assigneeId()).isEqualTo(11);
    }

    @Test
    void findById_throwsNotFoundWhenMissing() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_stampsInitialStatusIdOne() {
        TaskCreateDto dto = new TaskCreateDto(
                "Task", "desc", 2, 1, 11, 10, LocalDate.of(2026, 6, 1)
        );
        when(projectRepository.findById(1)).thenReturn(Optional.of(project(1)));
        when(priorityRepository.findById(2)).thenReturn(Optional.of(priority(2)));
        when(userRepository.findById(10)).thenReturn(Optional.of(user(10)));
        when(userRepository.findById(11)).thenReturn(Optional.of(user(11)));
        when(taskStatusRepository.findById(TaskService.INITIAL_STATUS_ID))
                .thenReturn(Optional.of(status(1)));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(42);
            return t;
        });

        TaskDto result = taskService.create(dto);

        assertThat(result.id()).isEqualTo(42);
        assertThat(result.statusId()).isEqualTo(1);
        assertThat(result.assigneeId()).isEqualTo(11);
        assertThat(result.creatorId()).isEqualTo(10);
        assertThat(result.deadline()).isEqualTo(LocalDate.of(2026, 6, 1));
    }

    @Test
    void create_acceptsNullAssignee() {
        TaskCreateDto dto = new TaskCreateDto(
                "Task", null, 2, 1, null, 10, null
        );
        when(projectRepository.findById(1)).thenReturn(Optional.of(project(1)));
        when(priorityRepository.findById(2)).thenReturn(Optional.of(priority(2)));
        when(userRepository.findById(10)).thenReturn(Optional.of(user(10)));
        when(taskStatusRepository.findById(TaskService.INITIAL_STATUS_ID))
                .thenReturn(Optional.of(status(1)));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskDto result = taskService.create(dto);

        assertThat(result.assigneeId()).isNull();
        // assignee user lookup must NOT have been performed
        verify(userRepository, never()).findById(11);
    }

    @Test
    void create_throwsNotFoundWhenProjectMissing() {
        TaskCreateDto dto = new TaskCreateDto("T", null, 1, 99, null, 10, null);
        when(projectRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void create_throwsNotFoundWhenPriorityMissing() {
        TaskCreateDto dto = new TaskCreateDto("T", null, 99, 1, null, 10, null);
        when(projectRepository.findById(1)).thenReturn(Optional.of(project(1)));
        when(priorityRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_throwsNotFoundWhenCreatorMissing() {
        TaskCreateDto dto = new TaskCreateDto("T", null, 1, 1, null, 99, null);
        when(projectRepository.findById(1)).thenReturn(Optional.of(project(1)));
        when(priorityRepository.findById(1)).thenReturn(Optional.of(priority(1)));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_throwsNotFoundWhenAssigneeProvidedButMissing() {
        TaskCreateDto dto = new TaskCreateDto("T", null, 1, 1, 99, 10, null);
        when(projectRepository.findById(1)).thenReturn(Optional.of(project(1)));
        when(priorityRepository.findById(1)).thenReturn(Optional.of(priority(1)));
        when(userRepository.findById(10)).thenReturn(Optional.of(user(10)));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_replacesAllFieldsIncludingStatus() {
        Task existing = task(1, project(1), priority(1), status(1), user(10), user(11));
        when(taskRepository.findById(1)).thenReturn(Optional.of(existing));
        when(projectRepository.findById(2)).thenReturn(Optional.of(project(2)));
        when(priorityRepository.findById(3)).thenReturn(Optional.of(priority(3)));
        when(taskStatusRepository.findById(4)).thenReturn(Optional.of(status(4)));
        when(userRepository.findById(20)).thenReturn(Optional.of(user(20)));
        when(userRepository.findById(21)).thenReturn(Optional.of(user(21)));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskUpdateDto dto = new TaskUpdateDto(
                "New name", "new desc", 3, 4, 2, 21, 20, LocalDate.of(2026, 9, 1)
        );

        TaskDto result = taskService.update(1, dto);

        assertThat(result.name()).isEqualTo("New name");
        assertThat(result.priorityId()).isEqualTo(3);
        assertThat(result.statusId()).isEqualTo(4);
        assertThat(result.projectId()).isEqualTo(2);
        assertThat(result.assigneeId()).isEqualTo(21);
        assertThat(result.creatorId()).isEqualTo(20);
        assertThat(result.deadline()).isEqualTo(LocalDate.of(2026, 9, 1));
    }

    @Test
    void update_clearsAssigneeWhenNull() {
        Task existing = task(1, project(1), priority(1), status(1), user(10), user(11));
        when(taskRepository.findById(1)).thenReturn(Optional.of(existing));
        when(projectRepository.findById(1)).thenReturn(Optional.of(project(1)));
        when(priorityRepository.findById(1)).thenReturn(Optional.of(priority(1)));
        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(status(1)));
        when(userRepository.findById(10)).thenReturn(Optional.of(user(10)));

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        TaskUpdateDto dto = new TaskUpdateDto("N", null, 1, 1, 1, null, 10, null);

        taskService.update(1, dto);

        assertThat(captor.getValue().getAssignee()).isNull();
    }

    @Test
    void update_throwsNotFoundWhenTaskMissing() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        TaskUpdateDto dto = new TaskUpdateDto("N", null, 1, 1, 1, null, 10, null);

        assertThatThrownBy(() -> taskService.update(99, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_removesTask() {
        Task t = task(1, project(1), priority(1), status(1), user(10), null);
        when(taskRepository.findById(1)).thenReturn(Optional.of(t));

        taskService.delete(1);

        verify(taskRepository).delete(t);
    }

    @Test
    void delete_throwsNotFoundWhenMissing() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
