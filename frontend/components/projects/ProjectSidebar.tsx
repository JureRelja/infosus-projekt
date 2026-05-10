"use client";

import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useSupStore } from "@/lib/store/SupStore";

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("hr-HR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

function initials(firstName: string, lastName: string) {
  return `${firstName[0] ?? ""}${lastName[0] ?? ""}`.toUpperCase();
}

export function ProjectSidebar({ projectId }: { projectId: number }) {
  const {
    getProjectById,
    getUserById,
    projectStatuses,
    updateProjectStatus,
  } = useSupStore();
  const project = getProjectById(projectId);
  if (!project) return null;
  const manager = getUserById(project.managerId);
  const members = project.memberIds
    .map((id) => getUserById(id))
    .filter((u): u is NonNullable<typeof u> => Boolean(u));

  return (
    <aside className="space-y-6 rounded-lg border border-border bg-card p-5">
      <div className="space-y-2">
        <Label>Status</Label>
        <Select
          value={String(project.statusId)}
          onValueChange={(v) => updateProjectStatus(projectId, Number(v))}
        >
          <SelectTrigger className="w-full">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            {projectStatuses.map((s) => (
              <SelectItem key={s.id} value={String(s.id)}>
                {s.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="space-y-1">
        <Label>Menadžer</Label>
        <p className="text-sm text-foreground">
          {manager ? `${manager.firstName} ${manager.lastName}` : "—"}
        </p>
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div className="space-y-1">
          <Label>Početak</Label>
          <p className="text-sm text-foreground">
            {formatDate(project.startDate)}
          </p>
        </div>
        <div className="space-y-1">
          <Label>Završetak</Label>
          <p className="text-sm text-foreground">
            {formatDate(project.endDate)}
          </p>
        </div>
      </div>

      <div className="space-y-2">
        <Label>Članovi tima</Label>
        <ul className="space-y-1.5 text-sm">
          {members.map((m) => (
            <li key={m.id} className="flex items-center gap-2">
              <span className="flex size-6 shrink-0 items-center justify-center rounded-full bg-muted text-xs font-medium text-muted-foreground">
                {initials(m.firstName, m.lastName)}
              </span>
              <span className="text-foreground">
                {m.firstName} {m.lastName}
              </span>
            </li>
          ))}
        </ul>
      </div>
    </aside>
  );
}
