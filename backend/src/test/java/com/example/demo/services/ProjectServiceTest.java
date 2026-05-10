package com.example.demo.services;

import com.example.demo.dtos.ProjectCreateDto;
import com.example.demo.dtos.ProjectDescriptionPatchDto;
import com.example.demo.dtos.ProjectDto;
import com.example.demo.dtos.ProjectStatusPatchDto;
import com.example.demo.dtos.ProjectUpdateDto;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Project;
import com.example.demo.models.ProjectMember;
import com.example.demo.models.ProjectStatus;
import com.example.demo.models.User;
import com.example.demo.repositories.ProjectMemberRepository;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.ProjectStatusRepository;
import com.example.demo.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private ProjectStatusRepository projectStatusRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private static ProjectStatus status(Integer id) {
        ProjectStatus s = new ProjectStatus();
        s.setId(id);
        s.setName("Status" + id);
        return s;
    }

    private static User user(Integer id) {
        User u = new User();
        u.setId(id);
        return u;
    }

    private static Project project(Integer id, ProjectStatus s, User mgr) {
        Project p = new Project();
        p.setId(id);
        p.setName("P" + id);
        p.setDescription("desc");
        p.setStartDate(LocalDate.of(2026, 1, 1));
        p.setEndDate(LocalDate.of(2026, 12, 31));
        p.setStatus(s);
        p.setManager(mgr);
        return p;
    }

    private static ProjectMember member(Integer id, Project p, User u) {
        ProjectMember pm = new ProjectMember();
        pm.setId(id);
        pm.setProject(p);
        pm.setUser(u);
        return pm;
    }

    @Test
    void findAll_returnsAllProjectsWithMemberIds() {
        Project p1 = project(1, status(1), user(10));
        Project p2 = project(2, status(2), user(11));
        when(projectRepository.findAll()).thenReturn(List.of(p1, p2));
        when(projectMemberRepository.findByProjectId(1))
                .thenReturn(List.of(member(100, p1, user(20)), member(101, p1, user(21))));
        when(projectMemberRepository.findByProjectId(2)).thenReturn(List.of());

        List<ProjectDto> result = projectService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).memberIds()).containsExactly(20, 21);
        assertThat(result.get(1).memberIds()).isEmpty();
    }

    @Test
    void findById_returnsProject() {
        Project p = project(1, status(1), user(10));
        when(projectRepository.findById(1)).thenReturn(Optional.of(p));
        when(projectMemberRepository.findByProjectId(1)).thenReturn(List.of());

        ProjectDto result = projectService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.statusId()).isEqualTo(1);
        assertThat(result.managerId()).isEqualTo(10);
    }

    @Test
    void findById_throwsNotFoundWhenMissing() {
        when(projectRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_persistsProjectAndMembers() {
        ProjectCreateDto dto = new ProjectCreateDto(
                "New", "desc",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 1),
                1, 10, List.of(20, 21)
        );
        when(projectStatusRepository.findById(1)).thenReturn(Optional.of(status(1)));
        when(userRepository.findById(10)).thenReturn(Optional.of(user(10)));
        when(userRepository.findById(20)).thenReturn(Optional.of(user(20)));
        when(userRepository.findById(21)).thenReturn(Optional.of(user(21)));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> {
            Project saved = inv.getArgument(0);
            saved.setId(1);
            return saved;
        });

        ProjectDto result = projectService.create(dto);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.memberIds()).containsExactly(20, 21);
        verify(projectRepository).save(any(Project.class));
        verify(projectMemberRepository, times(2)).save(any(ProjectMember.class));
    }

    @Test
    void create_throwsNotFoundWhenStatusMissing() {
        ProjectCreateDto dto = new ProjectCreateDto(
                "X", null, LocalDate.now(), LocalDate.now().plusDays(1),
                99, 10, List.of()
        );
        when(projectStatusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void create_throwsNotFoundWhenManagerMissing() {
        ProjectCreateDto dto = new ProjectCreateDto(
                "X", null, LocalDate.now(), LocalDate.now().plusDays(1),
                1, 99, List.of()
        );
        when(projectStatusRepository.findById(1)).thenReturn(Optional.of(status(1)));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_throwsNotFoundWhenMemberMissing() {
        ProjectCreateDto dto = new ProjectCreateDto(
                "X", null, LocalDate.now(), LocalDate.now().plusDays(1),
                1, 10, List.of(99)
        );
        when(projectStatusRepository.findById(1)).thenReturn(Optional.of(status(1)));
        when(userRepository.findById(10)).thenReturn(Optional.of(user(10)));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> {
            Project saved = inv.getArgument(0);
            saved.setId(1);
            return saved;
        });
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_acceptsNullMemberIds() {
        ProjectCreateDto dto = new ProjectCreateDto(
                "X", null, LocalDate.now(), LocalDate.now().plusDays(1),
                1, 10, null
        );
        when(projectStatusRepository.findById(1)).thenReturn(Optional.of(status(1)));
        when(userRepository.findById(10)).thenReturn(Optional.of(user(10)));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> {
            Project saved = inv.getArgument(0);
            saved.setId(1);
            return saved;
        });

        ProjectDto result = projectService.create(dto);

        assertThat(result.memberIds()).isEmpty();
        verify(projectMemberRepository, never()).save(any());
    }

    @Test
    void update_syncsMembersAddingAndRemoving() {
        Project existing = project(1, status(1), user(10));
        when(projectRepository.findById(1)).thenReturn(Optional.of(existing));
        when(projectStatusRepository.findById(2)).thenReturn(Optional.of(status(2)));
        when(userRepository.findById(11)).thenReturn(Optional.of(user(11)));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        // existing members: {20, 21, 22}; new target: {21, 22, 23}
        List<ProjectMember> currentMembers = new ArrayList<>(List.of(
                member(200, existing, user(20)),
                member(201, existing, user(21)),
                member(202, existing, user(22))
        ));
        when(projectMemberRepository.findByProjectId(1))
                .thenReturn(currentMembers)
                .thenReturn(List.of(
                        member(201, existing, user(21)),
                        member(202, existing, user(22)),
                        member(203, existing, user(23))
                ));
        when(userRepository.findById(23)).thenReturn(Optional.of(user(23)));

        ProjectUpdateDto dto = new ProjectUpdateDto(
                "Updated", "new desc",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                2, 11, List.of(21, 22, 23)
        );

        ProjectDto result = projectService.update(1, dto);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<Integer>> removeCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(projectMemberRepository).deleteByProjectIdAndUserIdIn(eq(1), removeCaptor.capture());
        assertThat(new HashSet<>(removeCaptor.getValue())).isEqualTo(Set.of(20));

        ArgumentCaptor<ProjectMember> addCaptor = ArgumentCaptor.forClass(ProjectMember.class);
        verify(projectMemberRepository, times(1)).save(addCaptor.capture());
        assertThat(addCaptor.getValue().getUser().getId()).isEqualTo(23);

        assertThat(result.memberIds()).containsExactlyInAnyOrder(21, 22, 23);
    }

    @Test
    void update_throwsNotFoundWhenProjectMissing() {
        when(projectRepository.findById(99)).thenReturn(Optional.empty());

        ProjectUpdateDto dto = new ProjectUpdateDto(
                "X", null, LocalDate.now(), LocalDate.now().plusDays(1),
                1, 1, List.of()
        );

        assertThatThrownBy(() -> projectService.update(99, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void patchStatus_updatesStatus() {
        Project p = project(1, status(1), user(10));
        when(projectRepository.findById(1)).thenReturn(Optional.of(p));
        when(projectStatusRepository.findById(2)).thenReturn(Optional.of(status(2)));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));
        when(projectMemberRepository.findByProjectId(1)).thenReturn(List.of());

        ProjectDto result = projectService.patchStatus(1, new ProjectStatusPatchDto(2));

        assertThat(result.statusId()).isEqualTo(2);
        assertThat(p.getStatus().getId()).isEqualTo(2);
    }

    @Test
    void patchStatus_throwsNotFoundWhenStatusMissing() {
        Project p = project(1, status(1), user(10));
        when(projectRepository.findById(1)).thenReturn(Optional.of(p));
        when(projectStatusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.patchStatus(1, new ProjectStatusPatchDto(99)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void patchDescription_setsValueOrNull() {
        Project p = project(1, status(1), user(10));
        when(projectRepository.findById(1)).thenReturn(Optional.of(p));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));
        when(projectMemberRepository.findByProjectId(1)).thenReturn(List.of());

        ProjectDto result = projectService.patchDescription(1, new ProjectDescriptionPatchDto("new"));
        assertThat(result.description()).isEqualTo("new");

        ProjectDto cleared = projectService.patchDescription(1, new ProjectDescriptionPatchDto(null));
        assertThat(cleared.description()).isNull();
    }

    @Test
    void delete_removesProject() {
        Project p = project(1, status(1), user(10));
        when(projectRepository.findById(1)).thenReturn(Optional.of(p));

        projectService.delete(1);

        verify(projectRepository).delete(p);
    }

    @Test
    void delete_throwsNotFoundWhenMissing() {
        when(projectRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(projectRepository, never()).delete(any());
    }
}
