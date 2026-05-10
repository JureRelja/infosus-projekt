"use client";

import { useMemo, useState } from "react";
import { isAxiosError } from "axios";
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

function backendMessage(error: unknown, fallback: string) {
  if (isAxiosError(error)) {
    const data = error.response?.data as { message?: string } | undefined;
    if (data?.message) return data.message;
  }
  return fallback;
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
  const [editingMembers, setEditingMembers] = useState(false);
  const [memberDraft, setMemberDraft] = useState<number[]>([]);
  const [memberAddPick, setMemberAddPick] = useState<string>("");

  const draftMembersResolved = useMemo(
    () =>
      memberDraft
        .map((id) => getUserById(id))
        .filter((u): u is NonNullable<ReturnType<typeof getUserById>> =>
          Boolean(u),
        ),
    [memberDraft, getUserById],
  );

  const candidatesToAdd = useMemo(() => {
    if (!project) return [];
    const taken = new Set<number>([project.managerId, ...memberDraft]);
    return users.filter((u) => !taken.has(u.id));
  }, [users, project, memberDraft]);

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

  const startMembersEdit = () => {
    setMemberDraft([...project.memberIds]);
    setMemberAddPick("");
    updateProject.reset();
    setEditingMembers(true);
  };

  const cancelMembersEdit = () => {
    setEditingMembers(false);
    updateProject.reset();
  };

  const addMember = () => {
    const id = Number(memberAddPick);
    if (!Number.isFinite(id) || memberDraft.includes(id)) return;
    setMemberDraft([...memberDraft, id]);
    setMemberAddPick("");
  };

  const removeMember = (id: number) => {
    setMemberDraft(memberDraft.filter((m) => m !== id));
  };

  const saveMembers = () => {
    const sortedCurrent = [...project.memberIds].sort((a, b) => a - b);
    const sortedDraft = [...memberDraft].sort((a, b) => a - b);
    const unchanged =
      sortedCurrent.length === sortedDraft.length &&
      sortedCurrent.every((id, i) => id === sortedDraft[i]);
    if (unchanged) {
      setEditingMembers(false);
      return;
    }
    updateProject.mutate(
      {
        name: project.name,
        description: project.description,
        startDate: project.startDate,
        endDate: project.endDate,
        statusId: project.statusId,
        managerId: project.managerId,
        memberIds: memberDraft,
      },
      { onSuccess: () => setEditingMembers(false) },
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
        {updateStatus.isError ? (
          <p className="text-xs text-destructive">
            {backendMessage(updateStatus.error, "Greška pri promjeni statusa.")}
          </p>
        ) : null}
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
            {updateProject.isError && editingDates ? (
              <p className="text-xs text-destructive">
                {backendMessage(
                  updateProject.error,
                  "Greška pri spremanju datuma.",
                )}
              </p>
            ) : null}
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
        <div className="flex items-center justify-between">
          <Label>Članovi tima</Label>
          {!editingMembers ? (
            <Button
              variant="ghost"
              size="sm"
              className="h-auto px-2 py-0.5 text-xs"
              onClick={startMembersEdit}
            >
              Uredi
            </Button>
          ) : null}
        </div>
        {editingMembers ? (
          <div className="space-y-2">
            {draftMembersResolved.length > 0 ? (
              <ul className="space-y-1.5 text-sm">
                {draftMembersResolved.map((m) => (
                  <li key={m.id} className="flex items-center gap-2">
                    <span className="flex size-6 shrink-0 items-center justify-center rounded-full bg-muted text-xs font-medium text-muted-foreground">
                      {initials(m.firstName, m.lastName)}
                    </span>
                    <span className="flex-1 text-foreground">
                      {m.firstName} {m.lastName}
                    </span>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="h-auto px-2 py-0.5 text-xs text-destructive"
                      onClick={() => removeMember(m.id)}
                      disabled={updateProject.isPending}
                    >
                      Ukloni
                    </Button>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="text-sm text-muted-foreground">Nema članova.</p>
            )}
            <div className="flex gap-2">
              <Select value={memberAddPick} onValueChange={setMemberAddPick}>
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Dodaj člana…" />
                </SelectTrigger>
                <SelectContent>
                  {candidatesToAdd.length === 0 ? (
                    <div className="px-2 py-1.5 text-xs text-muted-foreground">
                      Nema dostupnih korisnika
                    </div>
                  ) : (
                    candidatesToAdd.map((u) => (
                      <SelectItem key={u.id} value={String(u.id)}>
                        {u.firstName} {u.lastName}
                      </SelectItem>
                    ))
                  )}
                </SelectContent>
              </Select>
              <Button
                size="sm"
                variant="outline"
                onClick={addMember}
                disabled={!memberAddPick || updateProject.isPending}
              >
                Dodaj
              </Button>
            </div>
            {updateProject.isError ? (
              <p className="text-xs text-destructive">
                {backendMessage(
                  updateProject.error,
                  "Greška pri spremanju članova.",
                )}
              </p>
            ) : null}
            <div className="flex justify-end gap-2">
              <Button
                variant="ghost"
                size="sm"
                onClick={cancelMembersEdit}
                disabled={updateProject.isPending}
              >
                Odustani
              </Button>
              <Button
                size="sm"
                onClick={saveMembers}
                disabled={updateProject.isPending}
              >
                {updateProject.isPending ? "Spremanje…" : "Spremi"}
              </Button>
            </div>
          </div>
        ) : members.length > 0 ? (
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
        ) : (
          <p className="text-sm text-muted-foreground">Nema članova.</p>
        )}
      </div>
    </aside>
  );
}
