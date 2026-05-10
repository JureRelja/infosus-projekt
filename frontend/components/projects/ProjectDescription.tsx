"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import {
  useProject,
  useUpdateProjectDescription,
} from "@/lib/hooks/queries";

export function ProjectDescription({ projectId }: { projectId: number }) {
  const { data: project } = useProject(projectId);
  const update = useUpdateProjectDescription(projectId);
  const [editing, setEditing] = useState(false);
  const [draft, setDraft] = useState("");

  if (!project) return null;

  const start = () => {
    setDraft(project.description ?? "");
    setEditing(true);
  };

  const save = () => {
    update.mutate(draft.trim(), {
      onSuccess: () => setEditing(false),
    });
  };

  const cancel = () => {
    setDraft(project.description ?? "");
    setEditing(false);
  };

  return (
    <section className="space-y-3">
      <div className="flex items-center justify-between">
        <h2 className="text-sm font-medium text-muted-foreground">Opis</h2>
        {!editing ? (
          <Button variant="ghost" size="sm" onClick={start}>
            Uredi
          </Button>
        ) : null}
      </div>
      {editing ? (
        <div className="space-y-2">
          <Textarea
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            rows={5}
            placeholder="Opis projekta…"
            autoFocus
            disabled={update.isPending}
          />
          <div className="flex justify-end gap-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={cancel}
              disabled={update.isPending}
            >
              Odustani
            </Button>
            <Button size="sm" onClick={save} disabled={update.isPending}>
              {update.isPending ? "Spremanje…" : "Spremi"}
            </Button>
          </div>
        </div>
      ) : (
        <p className="whitespace-pre-wrap text-sm leading-relaxed text-foreground">
          {project.description || (
            <span className="text-muted-foreground italic">Nema opisa.</span>
          )}
        </p>
      )}
    </section>
  );
}
