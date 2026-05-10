package com.example.demo.services;

import com.example.demo.dtos.TaskCreateDto;
import com.example.demo.dtos.TaskDto;
import com.example.demo.dtos.TaskUpdateDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.TaskMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository,
            PriorityRepository priorityRepository,
            TaskStatusRepository taskStatusRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.priorityRepository = priorityRepository;
        this.taskStatusRepository = taskStatusRepository;
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
