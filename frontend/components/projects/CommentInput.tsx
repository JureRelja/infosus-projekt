"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { useSupStore } from "@/lib/store/SupStore";

export function CommentInput({ projectId }: { projectId: number }) {
  const { addComment } = useSupStore();
  const [text, setText] = useState("");

  const submit = () => {
    const trimmed = text.trim();
    if (!trimmed) return;
    addComment(projectId, trimmed);
    setText("");
  };

  return (
    <div className="space-y-2">
      <Textarea
        value={text}
        onChange={(e) => setText(e.target.value)}
        rows={3}
        placeholder="Napiši komentar…"
      />
      <div className="flex justify-end">
        <Button size="sm" onClick={submit} disabled={!text.trim()}>
          Komentiraj
        </Button>
      </div>
    </div>
  );
}
