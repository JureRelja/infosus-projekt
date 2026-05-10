export interface Role {
  id: number;
  name: string;
}

export interface ProjectStatus {
  id: number;
  name: string;
}

export interface TaskStatus {
  id: number;
  name: string;
  orderIndex: number;
}

export interface Priority {
  id: number;
  name: string;
  orderIndex: number;
}

export interface User {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  roleId: number;
}

export interface Project {
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

export interface Task {
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

export interface Comment {
  id: number;
  text: string;
  projectId: number | null;
  taskId: number | null;
  authorId: number;
  createdAt: string;
}
