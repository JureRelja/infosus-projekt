"use client";

import Link from "next/link";
import { Card } from "@/components/ui/card";
import { useSupStore } from "@/lib/store/SupStore";
import { ProjectStatusBadge } from "./StatusBadge";

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("hr-HR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

export function ProjectListTable() {
  const { projects, getUserById, getProjectStatus } = useSupStore();

  if (projects.length === 0) {
    return (
      <Card className="p-8 text-center text-muted-foreground">
        Nema projekata.
      </Card>
    );
  }

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-card">
      <table className="w-full text-sm">
        <thead className="bg-muted/50 text-muted-foreground">
          <tr>
            <th className="px-4 py-3 text-left font-medium">Projekt</th>
            <th className="px-4 py-3 text-left font-medium">Status</th>
            <th className="px-4 py-3 text-left font-medium">Menadžer</th>
            <th className="px-4 py-3 text-left font-medium">Trajanje</th>
          </tr>
        </thead>
        <tbody>
          {projects.map((project) => {
            const status = getProjectStatus(project.statusId);
            const manager = getUserById(project.managerId);
            return (
              <tr
                key={project.id}
                className="border-t border-border transition-colors hover:bg-muted/40"
              >
                <td className="px-4 py-3">
                  <Link
                    href={`/projects/${project.id}`}
                    className="block font-medium text-foreground hover:underline"
                  >
                    {project.name}
                  </Link>
                  <p className="line-clamp-1 text-xs text-muted-foreground">
                    {project.description}
                  </p>
                </td>
                <td className="px-4 py-3">
                  {status ? <ProjectStatusBadge name={status.name} /> : null}
                </td>
                <td className="px-4 py-3 text-foreground">
                  {manager ? `${manager.firstName} ${manager.lastName}` : "—"}
                </td>
                <td className="px-4 py-3 text-muted-foreground">
                  {formatDate(project.startDate)} – {formatDate(project.endDate)}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
