"use client";

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
import {
  useProject,
  useProjectStatuses,
  useUpdateProject,
  useUpdateProjectStatus,
  useUsers,
} from "@/lib/hooks/queries";
import { useUserById } from "@/lib/hooks/lookups";

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("hr-HR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

function toInputDate(iso: string) {
  return iso.slice(0, 10);
}

function initials(firstName: string, lastName: string) {
  return `${firstName[0] ?? ""}${lastName[0] ?? ""}`.toUpperCase();
}

export function ProjectSidebar({ projectId }: { projectId: number }) {
  const { data: project } = useProject(projectId);
  const { data: projectStatuses = [] } = useProjectStatuses();
  const { data: users = [] } = useUsers();
  const updateStatus = useUpdateProjectStatus(projectId);
  const updateProject = useUpdateProject(projectId);
  const getUserById = useUserById();

  const [editingManager, setEditingManager] = useState(false);
  const [managerDraft, setManagerDraft] = useState<string>("");
  const [editingDates, setEditingDates] = useState(false);
  const [startDraft, setStartDraft] = useState<string>("");
  const [endDraft, setEndDraft] = useState<string>("");

  if (!project) return null;
  const manager = getUserById(project.managerId);
  const members = project.memberIds
    .map((id) => getUserById(id))
    .filter((u): u is NonNullable<typeof u> => Boolean(u));

  const startManagerEdit = () => {
    setManagerDraft(String(project.managerId));
    setEditingManager(true);
  };

  const saveManager = () => {
    const nextId = Number(managerDraft);
    if (!Number.isFinite(nextId) || nextId === project.managerId) {
      setEditingManager(false);
      return;
    }
    updateProject.mutate(
      {
        name: project.name,
        description: project.description,
        startDate: project.startDate,
        endDate: project.endDate,
        statusId: project.statusId,
        managerId: nextId,
        memberIds: project.memberIds,
      },
      { onSuccess: () => setEditingManager(false) },
    );
  };

  const startDatesEdit = () => {
    setStartDraft(toInputDate(project.startDate));
    setEndDraft(toInputDate(project.endDate));
    setEditingDates(true);
  };

  const saveDates = () => {
    if (!startDraft || !endDraft) return;
    if (
      startDraft === toInputDate(project.startDate) &&
      endDraft === toInputDate(project.endDate)
    ) {
      setEditingDates(false);
      return;
    }
    updateProject.mutate(
      {
        name: project.name,
        description: project.description,
        startDate: startDraft,
        endDate: endDraft,
        statusId: project.statusId,
        managerId: project.managerId,
        memberIds: project.memberIds,
      },
      { onSuccess: () => setEditingDates(false) },
    );
  };

  return (
    <aside className="space-y-6 rounded-lg border border-border bg-card p-5">
      <div className="space-y-2">
        <Label>Status</Label>
        <Select
          value={String(project.statusId)}
          onValueChange={(v) => updateStatus.mutate(Number(v))}
          disabled={updateStatus.isPending}
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

      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <Label>Menadžer</Label>
          {!editingManager ? (
            <Button
              variant="ghost"
              size="sm"
              className="h-auto px-2 py-0.5 text-xs"
              onClick={startManagerEdit}
            >
              Uredi
            </Button>
          ) : null}
        </div>
        {editingManager ? (
          <div className="space-y-2">
            <Select value={managerDraft} onValueChange={setManagerDraft}>
              <SelectTrigger className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {users.map((u) => (
                  <SelectItem key={u.id} value={String(u.id)}>
                    {u.firstName} {u.lastName}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <div className="flex justify-end gap-2">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => setEditingManager(false)}
                disabled={updateProject.isPending}
              >
                Odustani
              </Button>
              <Button
                size="sm"
                onClick={saveManager}
                disabled={updateProject.isPending}
              >
                {updateProject.isPending ? "Spremanje…" : "Spremi"}
              </Button>
            </div>
          </div>
        ) : (
          <p className="text-sm text-foreground">
            {manager ? `${manager.firstName} ${manager.lastName}` : "—"}
          </p>
        )}
      </div>

      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <Label>Trajanje</Label>
          {!editingDates ? (
            <Button
              variant="ghost"
              size="sm"
              className="h-auto px-2 py-0.5 text-xs"
              onClick={startDatesEdit}
            >
              Uredi
            </Button>
          ) : null}
        </div>
        {editingDates ? (
          <div className="space-y-2">
            <div className="grid grid-cols-2 gap-2">
              <div className="space-y-1">
                <Label htmlFor="proj-start" className="text-xs">
                  Početak
                </Label>
                <Input
                  id="proj-start"
                  type="date"
                  value={startDraft}
                  max={endDraft || undefined}
                  onChange={(e) => setStartDraft(e.target.value)}
                />
              </div>
              <div className="space-y-1">
                <Label htmlFor="proj-end" className="text-xs">
                  Završetak
                </Label>
                <Input
                  id="proj-end"
                  type="date"
                  value={endDraft}
                  min={startDraft || undefined}
                  onChange={(e) => setEndDraft(e.target.value)}
                />
              </div>
            </div>
            <div className="flex justify-end gap-2">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => setEditingDates(false)}
                disabled={updateProject.isPending}
              >
                Odustani
              </Button>
              <Button
                size="sm"
                onClick={saveDates}
                disabled={
                  updateProject.isPending ||
                  !startDraft ||
                  !endDraft ||
                  startDraft > endDraft
                }
              >
                {updateProject.isPending ? "Spremanje…" : "Spremi"}
              </Button>
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1">
              <Label className="text-xs">Početak</Label>
              <p className="text-sm text-foreground">
                {formatDate(project.startDate)}
              </p>
            </div>
            <div className="space-y-1">
              <Label className="text-xs">Završetak</Label>
              <p className="text-sm text-foreground">
                {formatDate(project.endDate)}
              </p>
            </div>
          </div>
        )}
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
