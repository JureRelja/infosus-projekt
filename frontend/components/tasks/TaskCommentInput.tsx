"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { useCreateComment } from "@/lib/hooks/queries";
import { CURRENT_USER_ID } from "@/lib/currentUser";

export function TaskCommentInput({ taskId }: { taskId: number }) {
  const createComment = useCreateComment(taskId);
  const [text, setText] = useState("");

  const submit = () => {
    const trimmed = text.trim();
    if (!trimmed) return;
    createComment.mutate(
      { text: trimmed, taskId, authorId: CURRENT_USER_ID },
      { onSuccess: () => setText("") },
    );
  };

  return (
    <div className="space-y-2">
      <Textarea
        value={text}
        onChange={(e) => setText(e.target.value)}
        rows={3}
        placeholder="Napiši komentar…"
        disabled={createComment.isPending}
      />
      <div className="flex justify-end">
        <Button
          size="sm"
          onClick={submit}
          disabled={!text.trim() || createComment.isPending}
        >
          {createComment.isPending ? "Slanje…" : "Komentiraj"}
        </Button>
      </div>
    </div>
  );
}
