"use client";

import { useSupStore } from "@/lib/store/SupStore";
import {
  PriorityBadge,
  TaskStatusBadge,
} from "@/components/projects/StatusBadge";

function formatDate(iso: string | null) {
  if (!iso) return "—";
  return new Date(iso).toLocaleDateString("hr-HR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

export function TaskList({ projectId }: { projectId: number }) {
  const { tasks, getUserById, getTaskStatus, getPriority } = useSupStore();
  const projectTasks = tasks
    .filter((t) => t.projectId === projectId)
    .sort((a, b) => a.id - b.id);

  if (projectTasks.length === 0) {
    return (
      <p className="text-sm text-muted-foreground italic">
        Nema zadataka na ovom projektu.
      </p>
    );
  }

  return (
    <div className="overflow-hidden rounded-lg border border-border">
      <table className="w-full text-sm">
        <thead className="bg-muted/50 text-muted-foreground">
          <tr>
            <th className="px-4 py-2 text-left font-medium">Zadatak</th>
            <th className="px-4 py-2 text-left font-medium">Status</th>
            <th className="px-4 py-2 text-left font-medium">Prioritet</th>
            <th className="px-4 py-2 text-left font-medium">Dodijeljeni</th>
            <th className="px-4 py-2 text-left font-medium">Rok</th>
          </tr>
        </thead>
        <tbody>
          {projectTasks.map((task) => {
            const status = getTaskStatus(task.statusId);
            const priority = getPriority(task.priorityId);
            const assignee = getUserById(task.assigneeId);
            return (
              <tr key={task.id} className="border-t border-border">
                <td className="px-4 py-2.5 text-foreground">{task.name}</td>
                <td className="px-4 py-2.5">
                  {status ? <TaskStatusBadge name={status.name} /> : null}
                </td>
                <td className="px-4 py-2.5">
                  {priority ? <PriorityBadge name={priority.name} /> : null}
                </td>
                <td className="px-4 py-2.5 text-foreground">
                  {assignee
                    ? `${assignee.firstName} ${assignee.lastName}`
                    : "—"}
                </td>
                <td className="px-4 py-2.5 text-muted-foreground">
                  {formatDate(task.deadline)}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
