"use client";

import { TaskList } from "@/components/tasks/TaskList";
import { AddTaskDialog } from "@/components/tasks/AddTaskDialog";

export function ProjectTabs({ projectId }: { projectId: number }) {
  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-sm font-medium text-muted-foreground">Zadaci</h2>
        <AddTaskDialog projectId={projectId} />
      </div>
      <TaskList projectId={projectId} />
    </section>
  );
}
