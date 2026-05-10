"use client";

import {
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import {
  commentsApi,
  lookupsApi,
  projectsApi,
  tasksApi,
  usersApi,
} from "@/lib/api/client";
import type {
  CommentCreateDto,
  ProjectCreateDto,
  ProjectUpdateDto,
  TaskCreateDto,
  TaskUpdateDto,
} from "@/lib/api/types";

const STATIC_STALE = 5 * 60 * 1000;

export function useProjects() {
  return useQuery({
    queryKey: ["projects"],
    queryFn: projectsApi.list,
  });
}

export function useProject(id: number) {
  return useQuery({
    queryKey: ["projects", id],
    queryFn: () => projectsApi.get(id),
    enabled: Number.isFinite(id),
  });
}

export function useTasksByProject(projectId: number) {
  return useQuery({
    queryKey: ["tasks", { projectId }],
    queryFn: () => tasksApi.list(projectId),
    enabled: Number.isFinite(projectId),
  });
}

export function useTaskComments(taskId: number | null) {
  return useQuery({
    queryKey: ["comments", { taskId }],
    queryFn: () => commentsApi.listByTask(taskId as number),
    enabled: taskId != null,
  });
}

export function useUsers() {
  return useQuery({
    queryKey: ["users"],
    queryFn: usersApi.list,
    staleTime: STATIC_STALE,
  });
}

export function usePriorities() {
  return useQuery({
    queryKey: ["priorities"],
    queryFn: lookupsApi.priorities,
    staleTime: STATIC_STALE,
  });
}

export function useProjectStatuses() {
  return useQuery({
    queryKey: ["project-statuses"],
    queryFn: lookupsApi.projectStatuses,
    staleTime: STATIC_STALE,
  });
}

export function useTaskStatuses() {
  return useQuery({
    queryKey: ["task-statuses"],
    queryFn: lookupsApi.taskStatuses,
    staleTime: STATIC_STALE,
  });
}

export function useUpdateProjectDescription(projectId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (description: string) =>
      projectsApi.patchDescription(projectId, description),
    onSuccess: (project) => {
      qc.setQueryData(["projects", projectId], project);
      qc.invalidateQueries({ queryKey: ["projects"] });
    },
  });
}

export function useUpdateProjectStatus(projectId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (statusId: number) =>
      projectsApi.patchStatus(projectId, statusId),
    onSuccess: (project) => {
      qc.setQueryData(["projects", projectId], project);
      qc.invalidateQueries({ queryKey: ["projects"] });
    },
  });
}

export function useCreateProject() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (dto: ProjectCreateDto) => projectsApi.create(dto),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["projects"] });
    },
  });
}

export function useUpdateProject(id: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (dto: ProjectUpdateDto) => projectsApi.update(id, dto),
    onSuccess: (project) => {
      qc.setQueryData(["projects", id], project);
      qc.invalidateQueries({ queryKey: ["projects"] });
    },
  });
}

export function useCreateTask(projectId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (dto: TaskCreateDto) => tasksApi.create(dto),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["tasks", { projectId }] });
    },
  });
}

export function useUpdateTask(taskId: number, projectId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (dto: TaskUpdateDto) => tasksApi.update(taskId, dto),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["tasks", { projectId }] });
    },
  });
}

export function useCreateComment(taskId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (dto: CommentCreateDto) => commentsApi.create(dto),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["comments", { taskId }] });
    },
  });
}
