export interface RoleDto {
  id: number;
  name: string;
}

export interface ProjectStatusDto {
  id: number;
  name: string;
}

export interface TaskStatusDto {
  id: number;
  name: string;
  orderIndex: number;
}

export interface PriorityDto {
  id: number;
  name: string;
  orderIndex: number;
}

export interface UserDto {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  roleId: number;
}

export interface ProjectDto {
  id: number;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  statusId: number;
  managerId: number;
  memberIds: number[];
  createdAt: string;
}

export interface TaskDto {
  id: number;
  name: string;
  description: string;
  priorityId: number;
  statusId: number;
  projectId: number;
  assigneeId: number | null;
  creatorId: number;
  deadline: string | null;
  createdAt: string;
}

export interface CommentDto {
  id: number;
  text: string;
  taskId: number;
  authorId: number;
  createdAt: string;
}

export interface ProjectCreateDto {
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  statusId: number;
  managerId: number;
  memberIds: number[];
}

export type ProjectUpdateDto = ProjectCreateDto;

export interface TaskCreateDto {
  name: string;
  description: string;
  priorityId: number;
  projectId: number;
  assigneeId: number | null;
  creatorId: number;
  deadline: string | null;
}

export interface TaskUpdateDto extends TaskCreateDto {
  statusId: number;
}

export interface CommentCreateDto {
  text: string;
  taskId: number;
  authorId: number;
}

export interface CommentUpdateDto {
  text: string;
}
