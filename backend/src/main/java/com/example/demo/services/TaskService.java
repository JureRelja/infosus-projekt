package com.example.demo.services;

import com.example.demo.dtos.TaskCreateDto;
import com.example.demo.dtos.TaskDto;
import com.example.demo.dtos.TaskUpdateDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.TaskMapper;
import com.example.demo.models.Priority;
import com.example.demo.models.Project;
import com.example.demo.models.ProjectMember;
import com.example.demo.models.Task;
import com.example.demo.models.TaskStatus;
import com.example.demo.models.User;
import com.example.demo.repositories.PriorityRepository;
import com.example.demo.repositories.ProjectMemberRepository;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.TaskStatusRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskService {

    public static final Integer INITIAL_STATUS_ID = 1;

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PriorityRepository priorityRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository,
            PriorityRepository priorityRepository,
            TaskStatusRepository taskStatusRepository,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.priorityRepository = priorityRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public List<TaskDto> findAll() {
        return taskRepository.findAll().stream()
                .map(TaskMapper::toDto)
                .toList();
    }

    public List<TaskDto> findByProjectId(Integer projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(TaskMapper::toDto)
                .toList();
    }

    public TaskDto findById(Integer id) {
        return TaskMapper.toDto(getEntityById(id));
    }

    @Transactional
    public TaskDto create(TaskCreateDto dto) {
        Project project = requireProject(dto.projectId());
        Priority priority = requirePriority(dto.priorityId());
        User creator = requireUser(dto.creatorId(), "Creator");
        User assignee = dto.assigneeId() != null ? requireUser(dto.assigneeId(), "Assignee") : null;
        TaskStatus initialStatus = taskStatusRepository.findById(INITIAL_STATUS_ID)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Initial TaskStatus with id " + INITIAL_STATUS_ID + " not found"));

        validateAssigneeIsProjectMember(project, assignee);
        validateDeadlineWithinProjectRange(project, dto.deadline());

        Task task = new Task();
        task.setName(dto.name());
        task.setDescription(dto.description());
        task.setPriority(priority);
        task.setStatus(initialStatus);
        task.setProject(project);
        task.setAssignee(assignee);
        task.setCreator(creator);
        task.setDeadline(dto.deadline());
        Task saved = taskRepository.save(task);
        return TaskMapper.toDto(saved);
    }

    @Transactional
    public TaskDto update(Integer id, TaskUpdateDto dto) {
        Task task = getEntityById(id);
        Project project = requireProject(dto.projectId());
        Priority priority = requirePriority(dto.priorityId());
        TaskStatus status = requireTaskStatus(dto.statusId());
        User creator = requireUser(dto.creatorId(), "Creator");
        User assignee = dto.assigneeId() != null ? requireUser(dto.assigneeId(), "Assignee") : null;

        validateAssigneeIsProjectMember(project, assignee);
        validateDeadlineWithinProjectRange(project, dto.deadline());

        task.setName(dto.name());
        task.setDescription(dto.description());
        task.setPriority(priority);
        task.setStatus(status);
        task.setProject(project);
        task.setAssignee(assignee);
        task.setCreator(creator);
        task.setDeadline(dto.deadline());
        return TaskMapper.toDto(taskRepository.save(task));
    }

    private void validateAssigneeIsProjectMember(Project project, User assignee) {
        if (assignee == null) return;
        Integer assigneeId = assignee.getId();
        if (project.getManager() != null && assigneeId.equals(project.getManager().getId())) {
            return;
        }
        boolean isMember = projectMemberRepository.findByProjectId(project.getId()).stream()
                .map(ProjectMember::getUser)
                .map(User::getId)
                .anyMatch(assigneeId::equals);
        if (!isMember) {
            throw new IllegalArgumentException(
                    "Korisnik (id " + assigneeId + ") nije član projekta i ne može biti dodijeljen zadatku.");
        }
    }

    private void validateDeadlineWithinProjectRange(Project project, LocalDate deadline) {
        if (deadline == null) return;
        LocalDate start = project.getStartDate();
        LocalDate end = project.getEndDate();
        if ((start != null && deadline.isBefore(start)) || (end != null && deadline.isAfter(end))) {
            throw new IllegalArgumentException(
                    "Rok zadatka (" + deadline + ") mora biti unutar trajanja projekta ("
                            + start + " – " + end + ").");
        }
    }

    @Transactional
    public void delete(Integer id) {
        taskRepository.delete(getEntityById(id));
    }

    private Task getEntityById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
    }

    private Project requireProject(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + projectId + " not found"));
    }

    private Priority requirePriority(Integer priorityId) {
        return priorityRepository.findById(priorityId)
                .orElseThrow(() -> new ResourceNotFoundException("Priority with id " + priorityId + " not found"));
    }

    private TaskStatus requireTaskStatus(Integer statusId) {
        return taskStatusRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + statusId + " not found"));
    }

    private User requireUser(Integer userId, String role) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(role + " (user) with id " + userId + " not found"));
    }
}
