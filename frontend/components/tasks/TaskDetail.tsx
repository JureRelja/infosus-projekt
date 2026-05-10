"use client";

import Link from "next/link";
import { notFound } from "next/navigation";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Separator } from "@/components/ui/separator";
import { TaskCommentInput } from "@/components/tasks/TaskCommentInput";
import { TaskCommentList } from "@/components/tasks/TaskCommentList";
import {
  usePriorities,
  useProject,
  useTask,
  useTaskStatuses,
  useUpdateTask,
  useUsers,
} from "@/lib/hooks/queries";
import { useUserById } from "@/lib/hooks/lookups";
import type { TaskDto } from "@/lib/api/types";

const UNASSIGNED = "__unassigned__";

function formatDate(iso: string | null) {
  if (!iso) return "—";
  return new Date(iso).toLocaleDateString("hr-HR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

function toInputDate(iso: string | null) {
  return iso ? iso.slice(0, 10) : "";
}

export function TaskDetail({
  projectId,
  taskId,
}: {
  projectId: number;
  taskId: number;
}) {
  const { data: task, isLoading, isError } = useTask(taskId);
  const { data: project } = useProject(projectId);
  const { data: priorities = [] } = usePriorities();
  const { data: taskStatuses = [] } = useTaskStatuses();
  const { data: users = [] } = useUsers();
  const update = useUpdateTask(taskId, projectId);
  const getUserById = useUserById();

  const [editingDeadline, setEditingDeadline] = useState(false);
  const [deadlineDraft, setDeadlineDraft] = useState<string>("");

  if (isLoading) {
    return <p className="text-muted-foreground">Učitavanje…</p>;
  }
  if (isError || !task) {
    notFound();
  }
  if (task.projectId !== projectId) {
    notFound();
  }

  const creator = getUserById(task.creatorId);
  const memberOptions = (project?.memberIds ?? [])
    .map((id) => getUserById(id))
    .filter((u): u is NonNullable<typeof u> => Boolean(u));

  const persist = (
    patch: Partial<Pick<TaskDto, "statusId" | "priorityId" | "assigneeId" | "deadline">>,
    onSuccess?: () => void,
  ) => {
    update.mutate(
      {
        name: task.name,
        description: task.description,
        priorityId: patch.priorityId ?? task.priorityId,
        statusId: patch.statusId ?? task.statusId,
        projectId: task.projectId,
        assigneeId:
          patch.assigneeId !== undefined ? patch.assigneeId : task.assigneeId,
        creatorId: task.creatorId,
        deadline:
          patch.deadline !== undefined ? patch.deadline : task.deadline,
      },
      onSuccess ? { onSuccess } : undefined,
    );
  };

  const saveDeadline = () => {
    const next = deadlineDraft || null;
    if (next === task.deadline) {
      setEditingDeadline(false);
      return;
    }
    persist({ deadline: next }, () => setEditingDeadline(false));
  };

  return (
    <div className="space-y-6">
      <header className="space-y-2">
        <nav className="text-sm text-muted-foreground">
          <Link href="/projects" className="hover:underline">
            Projekti
          </Link>
          <span className="mx-2">/</span>
          <Link
            href={`/projects/${projectId}`}
            className="hover:underline"
          >
            {project?.name ?? `#${projectId}`}
          </Link>
          <span className="mx-2">/</span>
          <span>#{task.id}</span>
        </nav>
        <h1 className="text-2xl font-semibold tracking-tight">{task.name}</h1>
      </header>

      <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_280px]">
        <div className="space-y-6">
          <section className="space-y-2">
            <h2 className="text-sm font-medium text-muted-foreground">Opis</h2>
            <p className="whitespace-pre-wrap text-sm leading-relaxed text-foreground">
              {task.description || (
                <span className="text-muted-foreground italic">Nema opisa.</span>
              )}
            </p>
          </section>

          <Separator />

          <section className="space-y-3">
            <h2 className="text-sm font-medium text-muted-foreground">
              Komentari
            </h2>
            <TaskCommentInput taskId={task.id} />
            <TaskCommentList taskId={task.id} />
          </section>
        </div>

        <aside className="space-y-5 rounded-lg border border-border bg-card p-5">
          <div className="space-y-2">
            <Label>Status</Label>
            <Select
              value={String(task.statusId)}
              onValueChange={(v) => persist({ statusId: Number(v) })}
              disabled={update.isPending}
            >
              <SelectTrigger className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {taskStatuses.map((s) => (
                  <SelectItem key={s.id} value={String(s.id)}>
                    {s.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label>Prioritet</Label>
            <Select
              value={String(task.priorityId)}
              onValueChange={(v) => persist({ priorityId: Number(v) })}
              disabled={update.isPending}
            >
              <SelectTrigger className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {priorities.map((p) => (
                  <SelectItem key={p.id} value={String(p.id)}>
                    {p.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label>Dodijeljeni</Label>
            <Select
              value={task.assigneeId == null ? UNASSIGNED : String(task.assigneeId)}
              onValueChange={(v) =>
                persist({ assigneeId: v === UNASSIGNED ? null : Number(v) })
              }
              disabled={update.isPending}
            >
              <SelectTrigger className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value={UNASSIGNED}>— Nedodijeljeno —</SelectItem>
                {(memberOptions.length > 0 ? memberOptions : users).map((u) => (
                  <SelectItem key={u.id} value={String(u.id)}>
                    {u.firstName} {u.lastName}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <Label>Rok</Label>
              {!editingDeadline ? (
                <Button
                  variant="ghost"
                  size="sm"
                  className="h-auto px-2 py-0.5 text-xs"
                  onClick={() => {
                    setDeadlineDraft(toInputDate(task.deadline));
                    setEditingDeadline(true);
                  }}
                >
                  Uredi
                </Button>
              ) : null}
            </div>
            {editingDeadline ? (
              <div className="space-y-2">
                <Input
                  type="date"
                  value={deadlineDraft}
                  onChange={(e) => setDeadlineDraft(e.target.value)}
                />
                <div className="flex justify-end gap-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setEditingDeadline(false)}
                    disabled={update.isPending}
                  >
                    Odustani
                  </Button>
                  <Button
                    size="sm"
                    onClick={saveDeadline}
                    disabled={update.isPending}
                  >
                    {update.isPending ? "Spremanje…" : "Spremi"}
                  </Button>
                </div>
              </div>
            ) : (
              <p className="text-sm text-foreground">
                {formatDate(task.deadline)}
              </p>
            )}
          </div>

          <Separator />

          <div className="space-y-1">
            <Label>Kreirao</Label>
            <p className="text-sm text-foreground">
              {creator ? `${creator.firstName} ${creator.lastName}` : "—"}
            </p>
          </div>

          <div className="space-y-1">
            <Label>Kreirano</Label>
            <p className="text-sm text-foreground">
              {formatDate(task.createdAt)}
            </p>
          </div>
        </aside>
      </div>
    </div>
  );
}
