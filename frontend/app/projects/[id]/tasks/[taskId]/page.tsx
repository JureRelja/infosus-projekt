"use client";

import { use } from "react";
import { notFound } from "next/navigation";
import { TaskDetail } from "@/components/tasks/TaskDetail";

export default function TaskDetailPage({
  params,
}: {
  params: Promise<{ id: string; taskId: string }>;
}) {
  const { id, taskId } = use(params);
  const projectId = Number(id);
  const numericTaskId = Number(taskId);

  if (!Number.isFinite(projectId) || !Number.isFinite(numericTaskId)) {
    notFound();
  }

  return (
    <main className="mx-auto w-full max-w-4xl px-6 py-10">
      <TaskDetail projectId={projectId} taskId={numericTaskId} />
    </main>
  );
}
