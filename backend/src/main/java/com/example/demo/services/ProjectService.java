package com.example.demo.services;

import com.example.demo.dtos.ProjectCreateDto;
import com.example.demo.dtos.ProjectDescriptionPatchDto;
import com.example.demo.dtos.ProjectDto;
import com.example.demo.dtos.ProjectStatusPatchDto;
import com.example.demo.dtos.ProjectUpdateDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.ProjectMapper;
import com.example.demo.models.Project;
import com.example.demo.models.ProjectMember;
import com.example.demo.models.ProjectStatus;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repositories.ProjectMemberRepository;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.ProjectStatusRepository;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ProjectService {

    static final String COMPLETED_STATUS_NAME = "Završen";
    static final String CLOSED_TASK_STATUS_NAME = "Zatvoren";

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            ProjectStatusRepository projectStatusRepository,
            UserRepository userRepository,
            TaskRepository taskRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectStatusRepository = projectStatusRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public List<ProjectDto> findAll() {
        return projectRepository.findAll().stream()
                .map(p -> ProjectMapper.toDto(p, loadMemberIds(p.getId())))
                .toList();
    }

    public ProjectDto findById(Integer id) {
        Project project = getEntityById(id);
        return ProjectMapper.toDto(project, loadMemberIds(project.getId()));
    }

    @Transactional
    public ProjectDto create(ProjectCreateDto dto) {
        ProjectStatus status = requireStatus(dto.statusId());
        User manager = requireUser(dto.managerId(), "Manager");

        Project project = new Project();
        project.setName(dto.name());
        project.setDescription(dto.description());
        project.setStartDate(dto.startDate());
        project.setEndDate(dto.endDate());
        project.setStatus(status);
        project.setManager(manager);
        Project saved = projectRepository.save(project);

        List<Integer> memberIds = normalizeMemberIds(dto.memberIds());
        for (Integer userId : memberIds) {
            User member = requireUser(userId, "Member");
            ProjectMember pm = new ProjectMember();
            pm.setProject(saved);
            pm.setUser(member);
            projectMemberRepository.save(pm);
        }

        return ProjectMapper.toDto(saved, memberIds);
    }

    @Transactional
    public ProjectDto update(Integer id, ProjectUpdateDto dto) {
        Project project = getEntityById(id);
        ProjectStatus status = requireStatus(dto.statusId());
        User manager = requireUser(dto.managerId(), "Manager");

        validateProjectDateRange(id, dto.startDate(), dto.endDate());

        project.setName(dto.name());
        project.setDescription(dto.description());
        project.setStartDate(dto.startDate());
        project.setEndDate(dto.endDate());
        project.setStatus(status);
        project.setManager(manager);
        Project saved = projectRepository.save(project);

        syncMembers(saved, normalizeMemberIds(dto.memberIds()));
        return ProjectMapper.toDto(saved, loadMemberIds(saved.getId()));
    }

    @Transactional
    public ProjectDto patchStatus(Integer id, ProjectStatusPatchDto dto) {
        Project project = getEntityById(id);
        ProjectStatus newStatus = requireStatus(dto.statusId());
        validateCanCompleteProject(id, newStatus);
        project.setStatus(newStatus);
        Project saved = projectRepository.save(project);
        return ProjectMapper.toDto(saved, loadMemberIds(saved.getId()));
    }

    @Transactional
    public ProjectDto patchDescription(Integer id, ProjectDescriptionPatchDto dto) {
        Project project = getEntityById(id);
        project.setDescription(dto.description());
        Project saved = projectRepository.save(project);
        return ProjectMapper.toDto(saved, loadMemberIds(saved.getId()));
    }

    @Transactional
    public void delete(Integer id) {
        Project project = getEntityById(id);
        projectRepository.delete(project);
    }

    private void validateCanCompleteProject(Integer projectId, ProjectStatus newStatus) {
        if (newStatus == null || !COMPLETED_STATUS_NAME.equalsIgnoreCase(newStatus.getName())) {
            return;
        }
        List<Task> openTasks = taskRepository.findByProjectId(projectId).stream()
                .filter(t -> t.getStatus() == null
                        || !CLOSED_TASK_STATUS_NAME.equalsIgnoreCase(t.getStatus().getName()))
                .toList();
        if (!openTasks.isEmpty()) {
            throw new IllegalArgumentException(
                    "Projekt se ne može označiti kao završen — postoji " + openTasks.size()
                            + " nezavršenih zadataka.");
        }
    }

    private void validateProjectDateRange(Integer projectId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return;
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "Datum završetka projekta (" + endDate + ") ne može biti prije datuma početka ("
                            + startDate + ").");
        }
        LocalDate latestDeadline = taskRepository.findByProjectId(projectId).stream()
                .map(Task::getDeadline)
                .filter(d -> d != null)
                .max(LocalDate::compareTo)
                .orElse(null);
        if (latestDeadline != null && latestDeadline.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Datum završetka projekta (" + endDate
                            + ") mora pokrivati rok najkasnijeg zadatka (" + latestDeadline + ").");
        }
        LocalDate earliestDeadline = taskRepository.findByProjectId(projectId).stream()
                .map(Task::getDeadline)
                .filter(d -> d != null)
                .min(LocalDate::compareTo)
                .orElse(null);
        if (earliestDeadline != null && earliestDeadline.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "Datum početka projekta (" + startDate
                            + ") mora biti prije ili jednak roku najranijeg zadatka ("
                            + earliestDeadline + ").");
        }
    }

    private Project getEntityById(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found"));
    }

    private ProjectStatus requireStatus(Integer statusId) {
        return projectStatusRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectStatus with id " + statusId + " not found"));
    }

    private User requireUser(Integer userId, String role) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(role + " (user) with id " + userId + " not found"));
    }

    private List<Integer> loadMemberIds(Integer projectId) {
        return projectMemberRepository.findByProjectId(projectId).stream()
                .map(pm -> pm.getUser().getId())
                .toList();
    }

    private List<Integer> normalizeMemberIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return ids.stream().distinct().toList();
    }

    private void syncMembers(Project project, List<Integer> targetIds) {
        List<ProjectMember> existing = projectMemberRepository.findByProjectId(project.getId());
        Set<Integer> existingIds = new HashSet<>();
        for (ProjectMember pm : existing) {
            existingIds.add(pm.getUser().getId());
        }
        Set<Integer> targetSet = new HashSet<>(targetIds);

        Set<Integer> toRemove = new HashSet<>(existingIds);
        toRemove.removeAll(targetSet);
        if (!toRemove.isEmpty()) {
            projectMemberRepository.deleteByProjectIdAndUserIdIn(project.getId(), toRemove);
        }

        Set<Integer> toAdd = new HashSet<>(targetSet);
        toAdd.removeAll(existingIds);
        for (Integer userId : toAdd) {
            User member = requireUser(userId, "Member");
            ProjectMember pm = new ProjectMember();
            pm.setProject(project);
            pm.setUser(member);
            projectMemberRepository.save(pm);
        }
    }
}
