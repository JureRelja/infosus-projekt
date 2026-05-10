"use client";

import { useMemo } from "react";
import {
  usePriorities,
  useProjectStatuses,
  useTaskStatuses,
  useUsers,
} from "./queries";

export function useUserById() {
  const { data } = useUsers();
  return useMemo(() => {
    const map = new Map((data ?? []).map((u) => [u.id, u]));
    return (id: number | null | undefined) =>
      id == null ? undefined : map.get(id);
  }, [data]);
}

export function useProjectStatusById() {
  const { data } = useProjectStatuses();
  return useMemo(() => {
    const map = new Map((data ?? []).map((s) => [s.id, s]));
    return (id: number) => map.get(id);
  }, [data]);
}

export function useTaskStatusById() {
  const { data } = useTaskStatuses();
  return useMemo(() => {
    const map = new Map((data ?? []).map((s) => [s.id, s]));
    return (id: number) => map.get(id);
  }, [data]);
}

export function usePriorityById() {
  const { data } = usePriorities();
  return useMemo(() => {
    const map = new Map((data ?? []).map((p) => [p.id, p]));
    return (id: number) => map.get(id);
  }, [data]);
}
