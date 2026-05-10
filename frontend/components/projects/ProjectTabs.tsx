"use client";

import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from "@/components/ui/tabs";
import { CommentInput } from "./CommentInput";
import { CommentList } from "./CommentList";
import { TaskList } from "@/components/tasks/TaskList";
import { AddTaskDialog } from "@/components/tasks/AddTaskDialog";

export function ProjectTabs({ projectId }: { projectId: number }) {
  return (
    <Tabs defaultValue="tasks" className="w-full">
      <TabsList>
        <TabsTrigger value="tasks">Zadaci</TabsTrigger>
        <TabsTrigger value="comments">Komentari</TabsTrigger>
      </TabsList>
      <TabsContent value="tasks" className="space-y-4 pt-4">
        <div className="flex justify-end">
          <AddTaskDialog projectId={projectId} />
        </div>
        <TaskList projectId={projectId} />
      </TabsContent>
      <TabsContent value="comments" className="space-y-6 pt-4">
        <CommentInput projectId={projectId} />
        <CommentList projectId={projectId} />
      </TabsContent>
    </Tabs>
  );
}
