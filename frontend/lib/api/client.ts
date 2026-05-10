import { api } from "@/lib/axios";
import type {
  CommentCreateDto,
  CommentDto,
  CommentUpdateDto,
  PriorityDto,
  ProjectCreateDto,
  ProjectDto,
  ProjectStatusDto,
  ProjectUpdateDto,
  RoleDto,
  TaskCreateDto,
  TaskDto,
  TaskStatusDto,
  TaskUpdateDto,
  UserDto,
} from "./types";

export const projectsApi = {
  list: () => api.get<ProjectDto[]>("/projects").then((r) => r.data),
  get: (id: number) =>
    api.get<ProjectDto>(`/projects/${id}`).then((r) => r.data),
  create: (dto: ProjectCreateDto) =>
    api.post<ProjectDto>("/projects", dto).then((r) => r.data),
  update: (id: number, dto: ProjectUpdateDto) =>
    api.put<ProjectDto>(`/projects/${id}`, dto).then((r) => r.data),
  patchStatus: (id: number, statusId: number) =>
    api
      .patch<ProjectDto>(`/projects/${id}/status`, { statusId })
      .then((r) => r.data),
  patchDescription: (id: number, description: string) =>
    api
      .patch<ProjectDto>(`/projects/${id}/description`, { description })
      .then((r) => r.data),
  remove: (id: number) =>
    api.delete<void>(`/projects/${id}`).then((r) => r.data),
};

export const tasksApi = {
  list: (projectId?: number) =>
    api
      .get<TaskDto[]>("/tasks", {
        params: projectId != null ? { projectId } : undefined,
      })
      .then((r) => r.data),
  get: (id: number) => api.get<TaskDto>(`/tasks/${id}`).then((r) => r.data),
  create: (dto: TaskCreateDto) =>
    api.post<TaskDto>("/tasks", dto).then((r) => r.data),
  update: (id: number, dto: TaskUpdateDto) =>
    api.put<TaskDto>(`/tasks/${id}`, dto).then((r) => r.data),
  remove: (id: number) => api.delete<void>(`/tasks/${id}`).then((r) => r.data),
};

export const commentsApi = {
  listByTask: (taskId: number) =>
    api
      .get<CommentDto[]>("/comments", { params: { taskId } })
      .then((r) => r.data),
  create: (dto: CommentCreateDto) =>
    api.post<CommentDto>("/comments", dto).then((r) => r.data),
  update: (id: number, dto: CommentUpdateDto) =>
    api.put<CommentDto>(`/comments/${id}`, dto).then((r) => r.data),
  remove: (id: number) =>
    api.delete<void>(`/comments/${id}`).then((r) => r.data),
};

export const usersApi = {
  list: () => api.get<UserDto[]>("/users").then((r) => r.data),
};

export const lookupsApi = {
  priorities: () =>
    api.get<PriorityDto[]>("/priorities").then((r) => r.data),
  projectStatuses: () =>
    api.get<ProjectStatusDto[]>("/project-statuses").then((r) => r.data),
  taskStatuses: () =>
    api.get<TaskStatusDto[]>("/task-statuses").then((r) => r.data),
  roles: () => api.get<RoleDto[]>("/roles").then((r) => r.data),
};
