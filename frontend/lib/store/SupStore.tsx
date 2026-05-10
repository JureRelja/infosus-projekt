"use client";

import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useReducer,
  type ReactNode,
} from "react";
import {
  initialComments,
  initialProjects,
  initialTasks,
  priorities,
  projectStatuses,
  roles,
  taskStatuses,
  users,
} from "@/lib/mock/data";
import { CURRENT_USER_ID } from "@/lib/mock/currentUser";
import type {
  Comment,
  Priority,
  Project,
  ProjectStatus,
  Role,
  Task,
  TaskStatus,
  User,
} from "@/lib/mock/types";

interface SupState {
  projects: Project[];
  tasks: Task[];
  comments: Comment[];
  users: User[];
  roles: Role[];
  projectStatuses: ProjectStatus[];
  taskStatuses: TaskStatus[];
  priorities: Priority[];
}

type Action =
  | {
      type: "UPDATE_PROJECT_DESCRIPTION";
      projectId: number;
      description: string;
    }
  | { type: "UPDATE_PROJECT_STATUS"; projectId: number; statusId: number }
  | { type: "ADD_COMMENT"; projectId: number; text: string }
  | {
      type: "ADD_TASK";
      projectId: number;
      name: string;
      description: string;
      priorityId: number;
      assigneeId: number | null;
      deadline: string | null;
    };

const initialState: SupState = {
  projects: initialProjects,
  tasks: initialTasks,
  comments: initialComments,
  users,
  roles,
  projectStatuses,
  taskStatuses,
  priorities,
};

function reducer(state: SupState, action: Action): SupState {
  switch (action.type) {
    case "UPDATE_PROJECT_DESCRIPTION":
      return {
        ...state,
        projects: state.projects.map((p) =>
          p.id === action.projectId
            ? { ...p, description: action.description }
            : p,
        ),
      };
    case "UPDATE_PROJECT_STATUS":
      return {
        ...state,
        projects: state.projects.map((p) =>
          p.id === action.projectId ? { ...p, statusId: action.statusId } : p,
        ),
      };
    case "ADD_COMMENT": {
      const nextId =
        state.comments.reduce((m, c) => Math.max(m, c.id), 0) + 1;
      const newComment: Comment = {
        id: nextId,
        text: action.text,
        projectId: action.projectId,
        taskId: null,
        authorId: CURRENT_USER_ID,
        createdAt: new Date().toISOString(),
      };
      return { ...state, comments: [...state.comments, newComment] };
    }
    case "ADD_TASK": {
      const nextId = state.tasks.reduce((m, t) => Math.max(m, t.id), 0) + 1;
      const newTask: Task = {
        id: nextId,
        name: action.name,
        description: action.description,
        priorityId: action.priorityId,
        statusId: 1, // U pripremi (FR-04 inicijalno stanje)
        projectId: action.projectId,
        assigneeId: action.assigneeId,
        creatorId: CURRENT_USER_ID,
        deadline: action.deadline,
        createdAt: new Date().toISOString(),
      };
      return { ...state, tasks: [...state.tasks, newTask] };
    }
    default:
      return state;
  }
}

interface SupStoreValue extends SupState {
  updateProjectDescription: (projectId: number, description: string) => void;
  updateProjectStatus: (projectId: number, statusId: number) => void;
  addComment: (projectId: number, text: string) => void;
  addTask: (input: {
    projectId: number;
    name: string;
    description: string;
    priorityId: number;
    assigneeId: number | null;
    deadline: string | null;
  }) => void;
  getUserById: (id: number | null | undefined) => User | undefined;
  getProjectStatus: (id: number) => ProjectStatus | undefined;
  getTaskStatus: (id: number) => TaskStatus | undefined;
  getPriority: (id: number) => Priority | undefined;
  getProjectById: (id: number) => Project | undefined;
}

const SupStoreContext = createContext<SupStoreValue | null>(null);

export function SupStoreProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(reducer, initialState);

  const updateProjectDescription = useCallback(
    (projectId: number, description: string) =>
      dispatch({ type: "UPDATE_PROJECT_DESCRIPTION", projectId, description }),
    [],
  );

  const updateProjectStatus = useCallback(
    (projectId: number, statusId: number) =>
      dispatch({ type: "UPDATE_PROJECT_STATUS", projectId, statusId }),
    [],
  );

  const addComment = useCallback(
    (projectId: number, text: string) =>
      dispatch({ type: "ADD_COMMENT", projectId, text }),
    [],
  );

  const addTask = useCallback<SupStoreValue["addTask"]>(
    (input) => dispatch({ type: "ADD_TASK", ...input }),
    [],
  );

  const value = useMemo<SupStoreValue>(
    () => ({
      ...state,
      updateProjectDescription,
      updateProjectStatus,
      addComment,
      addTask,
      getUserById: (id) =>
        id == null ? undefined : state.users.find((u) => u.id === id),
      getProjectStatus: (id) =>
        state.projectStatuses.find((s) => s.id === id),
      getTaskStatus: (id) => state.taskStatuses.find((s) => s.id === id),
      getPriority: (id) => state.priorities.find((p) => p.id === id),
      getProjectById: (id) => state.projects.find((p) => p.id === id),
    }),
    [state, updateProjectDescription, updateProjectStatus, addComment, addTask],
  );

  return (
    <SupStoreContext.Provider value={value}>
      {children}
    </SupStoreContext.Provider>
  );
}

export function useSupStore(): SupStoreValue {
  const ctx = useContext(SupStoreContext);
  if (!ctx)
    throw new Error("useSupStore must be used inside <SupStoreProvider>");
  return ctx;
}
