import { ProjectListTable } from "@/components/projects/ProjectListTable";

export default function ProjectsPage() {
  return (
    <main className="mx-auto w-full max-w-6xl px-6 py-10">
      <header className="mb-6">
        <h1 className="text-2xl font-semibold tracking-tight">Projekti</h1>
        <p className="text-sm text-muted-foreground">
          Pregled svih projekata. Klikni na projekt za detalje.
        </p>
      </header>
      <ProjectListTable />
    </main>
  );
}
