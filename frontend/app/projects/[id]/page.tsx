"use client";

import { use } from "react";
import { notFound } from "next/navigation";
import { ProjectDetailHeader } from "@/components/projects/ProjectDetailHeader";
import { ProjectDescription } from "@/components/projects/ProjectDescription";
import { ProjectSidebar } from "@/components/projects/ProjectSidebar";
import { ProjectTabs } from "@/components/projects/ProjectTabs";
import { Separator } from "@/components/ui/separator";
import { useSupStore } from "@/lib/store/SupStore";

export default function ProjectDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = use(params);
  const projectId = Number(id);
  const { getProjectById } = useSupStore();
  const project = getProjectById(projectId);

  if (!Number.isFinite(projectId) || !project) {
    notFound();
  }

  return (
    <main className="mx-auto w-full max-w-6xl px-6 py-10">
      <div className="grid gap-8 lg:grid-cols-[minmax(0,1fr)_320px]">
        <div className="space-y-6">
          <ProjectDetailHeader
            projectId={project.id}
            projectName={project.name}
          />
          <ProjectDescription projectId={project.id} />
          <Separator />
          <ProjectTabs projectId={project.id} />
        </div>
        <ProjectSidebar projectId={project.id} />
      </div>
    </main>
  );
}
