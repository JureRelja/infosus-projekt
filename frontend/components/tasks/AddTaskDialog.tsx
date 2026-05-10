"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { useSupStore } from "@/lib/store/SupStore";

const UNASSIGNED = "__unassigned__";

export function AddTaskDialog({ projectId }: { projectId: number }) {
  const { priorities, getProjectById, getUserById, addTask } = useSupStore();
  const project = getProjectById(projectId);
  const [open, setOpen] = useState(false);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [priorityId, setPriorityId] = useState<string>("2");
  const [assigneeId, setAssigneeId] = useState<string>(UNASSIGNED);
  const [deadline, setDeadline] = useState<string>("");

  if (!project) return null;

  const memberOptions = project.memberIds
    .map((id) => getUserById(id))
    .filter((u): u is NonNullable<typeof u> => Boolean(u));

  const reset = () => {
    setName("");
    setDescription("");
    setPriorityId("2");
    setAssigneeId(UNASSIGNED);
    setDeadline("");
  };

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = name.trim();
    if (!trimmed) return;
    addTask({
      projectId,
      name: trimmed,
      description: description.trim(),
      priorityId: Number(priorityId),
      assigneeId: assigneeId === UNASSIGNED ? null : Number(assigneeId),
      deadline: deadline || null,
    });
    reset();
    setOpen(false);
  };

  return (
    <Dialog
      open={open}
      onOpenChange={(next) => {
        setOpen(next);
        if (!next) reset();
      }}
    >
      <DialogTrigger asChild>
        <Button size="sm">Dodaj zadatak</Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Novi zadatak</DialogTitle>
          <DialogDescription>
            Zadatak se dodaje na projekt <strong>{project.name}</strong>.
            Inicijalno stanje je &quot;U pripremi&quot;.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={submit} className="space-y-4">
          <div className="space-y-1.5">
            <Label htmlFor="task-name">Naziv *</Label>
            <Input
              id="task-name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              autoFocus
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="task-description">Opis</Label>
            <Textarea
              id="task-description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
            />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label>Prioritet</Label>
              <Select value={priorityId} onValueChange={setPriorityId}>
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
            <div className="space-y-1.5">
              <Label htmlFor="task-deadline">Rok</Label>
              <Input
                id="task-deadline"
                type="date"
                value={deadline}
                onChange={(e) => setDeadline(e.target.value)}
              />
            </div>
          </div>
          <div className="space-y-1.5">
            <Label>Dodijeljeni</Label>
            <Select value={assigneeId} onValueChange={setAssigneeId}>
              <SelectTrigger className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value={UNASSIGNED}>— Nedodijeljeno —</SelectItem>
                {memberOptions.map((m) => (
                  <SelectItem key={m.id} value={String(m.id)}>
                    {m.firstName} {m.lastName}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="ghost"
              onClick={() => setOpen(false)}
            >
              Odustani
            </Button>
            <Button type="submit" disabled={!name.trim()}>
              Spremi
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
