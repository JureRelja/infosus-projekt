"use client";

import { useMemo } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Separator } from "@/components/ui/separator";
import {
  PriorityBadge,
  TaskStatusBadge,
} from "@/components/projects/StatusBadge";
import { TaskCommentList } from "@/components/tasks/TaskCommentList";
import { TaskCommentInput } from "@/components/tasks/TaskCommentInput";
import { useTasksByProject } from "@/lib/hooks/queries";
import {
  usePriorityById,
  useTaskStatusById,
  useUserById,
} from "@/lib/hooks/lookups";

function formatDate(iso: string | null) {
  if (!iso) return "—";
  return new Date(iso).toLocaleDateString("hr-HR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

interface Props {
  taskId: number | null;
  projectId: number;
  onClose: () => void;
}

export function TaskDetailDialog({ taskId, projectId, onClose }: Props) {
  const { data: tasks } = useTasksByProject(projectId);
  const getUserById = useUserById();
  const getTaskStatus = useTaskStatusById();
  const getPriority = usePriorityById();

  const task = useMemo(
    () => tasks?.find((t) => t.id === taskId) ?? null,
    [tasks, taskId],
  );

  const open = taskId != null;

  return (
    <Dialog open={open} onOpenChange={(next) => !next && onClose()}>
      <DialogContent className="sm:max-w-2xl">
        {task ? (
          <>
            <DialogHeader>
              <DialogTitle>{task.name}</DialogTitle>
              <DialogDescription>
                Detalji zadatka i komentari.
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              <div className="flex flex-wrap items-center gap-2">
                {(() => {
                  const s = getTaskStatus(task.statusId);
                  return s ? <TaskStatusBadge name={s.name} /> : null;
                })()}
                {(() => {
                  const p = getPriority(task.priorityId);
                  return p ? <PriorityBadge name={p.name} /> : null;
                })()}
              </div>

              <p className="whitespace-pre-wrap text-sm leading-relaxed text-foreground">
                {task.description || (
                  <span className="text-muted-foreground italic">
                    Nema opisa.
                  </span>
                )}
              </p>

              <dl className="grid grid-cols-2 gap-3 text-sm">
                <div>
                  <dt className="text-xs font-medium text-muted-foreground">
                    Dodijeljeni
                  </dt>
                  <dd className="text-foreground">
                    {(() => {
                      const u = getUserById(task.assigneeId);
                      return u ? `${u.firstName} ${u.lastName}` : "—";
                    })()}
                  </dd>
                </div>
                <div>
                  <dt className="text-xs font-medium text-muted-foreground">
                    Kreirao
                  </dt>
                  <dd className="text-foreground">
                    {(() => {
                      const u = getUserById(task.creatorId);
                      return u ? `${u.firstName} ${u.lastName}` : "—";
                    })()}
                  </dd>
                </div>
                <div>
                  <dt className="text-xs font-medium text-muted-foreground">
                    Rok
                  </dt>
                  <dd className="text-foreground">
                    {formatDate(task.deadline)}
                  </dd>
                </div>
                <div>
                  <dt className="text-xs font-medium text-muted-foreground">
                    Kreirano
                  </dt>
                  <dd className="text-foreground">
                    {formatDate(task.createdAt)}
                  </dd>
                </div>
              </dl>

              <Separator />

              <div className="space-y-3">
                <h3 className="text-sm font-medium text-muted-foreground">
                  Komentari
                </h3>
                <TaskCommentInput taskId={task.id} />
                <TaskCommentList taskId={task.id} />
              </div>
            </div>
          </>
        ) : null}
      </DialogContent>
    </Dialog>
  );
}
