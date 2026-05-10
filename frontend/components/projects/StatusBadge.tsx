import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

const PROJECT_STATUS_STYLES: Record<string, string> = {
  Aktivan: "bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-200",
  Završen: "bg-blue-100 text-blue-800 dark:bg-blue-900/40 dark:text-blue-200",
  Obustavljan: "bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-200",
};

const TASK_STATUS_STYLES: Record<string, string> = {
  "U pripremi": "bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-200",
  "U postupku": "bg-blue-100 text-blue-800 dark:bg-blue-900/40 dark:text-blue-200",
  "Na provjeri": "bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-200",
  Zatvoren: "bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-200",
};

const PRIORITY_STYLES: Record<string, string> = {
  Nizak: "bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-200",
  Srednji: "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/40 dark:text-yellow-200",
  Visok: "bg-orange-100 text-orange-800 dark:bg-orange-900/40 dark:text-orange-200",
  Kritičan: "bg-red-100 text-red-800 dark:bg-red-900/40 dark:text-red-200",
};

export function ProjectStatusBadge({ name }: { name: string }) {
  return (
    <Badge variant="outline" className={cn(PROJECT_STATUS_STYLES[name])}>
      {name}
    </Badge>
  );
}

export function TaskStatusBadge({ name }: { name: string }) {
  return (
    <Badge variant="outline" className={cn(TASK_STATUS_STYLES[name])}>
      {name}
    </Badge>
  );
}

export function PriorityBadge({ name }: { name: string }) {
  return (
    <Badge variant="outline" className={cn(PRIORITY_STYLES[name])}>
      {name}
    </Badge>
  );
}
