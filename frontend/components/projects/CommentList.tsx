"use client";

import { useSupStore } from "@/lib/store/SupStore";

function initials(firstName: string, lastName: string) {
  return `${firstName[0] ?? ""}${lastName[0] ?? ""}`.toUpperCase();
}

function formatTime(iso: string) {
  return new Date(iso).toLocaleString("hr-HR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function CommentList({ projectId }: { projectId: number }) {
  const { comments, getUserById } = useSupStore();
  const projectComments = comments
    .filter((c) => c.projectId === projectId)
    .sort((a, b) => b.createdAt.localeCompare(a.createdAt));

  if (projectComments.length === 0) {
    return (
      <p className="text-sm text-muted-foreground italic">
        Nema komentara — budi prvi!
      </p>
    );
  }

  return (
    <ul className="space-y-4">
      {projectComments.map((c) => {
        const author = getUserById(c.authorId);
        return (
          <li key={c.id} className="flex gap-3">
            <span className="flex size-8 shrink-0 items-center justify-center rounded-full bg-muted text-xs font-medium text-muted-foreground">
              {author ? initials(author.firstName, author.lastName) : "?"}
            </span>
            <div className="flex-1 space-y-1">
              <div className="flex items-baseline gap-2 text-sm">
                <span className="font-medium text-foreground">
                  {author ? `${author.firstName} ${author.lastName}` : "Nepoznat"}
                </span>
                <span className="text-xs text-muted-foreground">
                  {formatTime(c.createdAt)}
                </span>
              </div>
              <p className="whitespace-pre-wrap text-sm text-foreground">
                {c.text}
              </p>
            </div>
          </li>
        );
      })}
    </ul>
  );
}
