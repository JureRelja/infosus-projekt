import Link from "next/link";

export function ProjectDetailHeader({
  projectId,
  projectName,
}: {
  projectId: number;
  projectName: string;
}) {
  return (
    <header className="space-y-2">
      <nav className="text-sm text-muted-foreground">
        <Link href="/projects" className="hover:underline">
          Projekti
        </Link>
        <span className="mx-2">/</span>
        <span>#{projectId}</span>
      </nav>
      <h1 className="text-2xl font-semibold tracking-tight">{projectName}</h1>
    </header>
  );
}
