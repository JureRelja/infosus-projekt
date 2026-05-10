import type {
  Comment,
  Priority,
  Project,
  ProjectStatus,
  Role,
  Task,
  TaskStatus,
  User,
} from "./types";

export const roles: Role[] = [
  { id: 1, name: "Direktor" },
  { id: 2, name: "Menadžer" },
  { id: 3, name: "Zaposleni" },
];

export const projectStatuses: ProjectStatus[] = [
  { id: 1, name: "Aktivan" },
  { id: 2, name: "Završen" },
  { id: 3, name: "Obustavljan" },
];

export const priorities: Priority[] = [
  { id: 1, name: "Nizak", orderIndex: 1 },
  { id: 2, name: "Srednji", orderIndex: 2 },
  { id: 3, name: "Visok", orderIndex: 3 },
  { id: 4, name: "Kritičan", orderIndex: 4 },
];

export const taskStatuses: TaskStatus[] = [
  { id: 1, name: "U pripremi", orderIndex: 1 },
  { id: 2, name: "U postupku", orderIndex: 2 },
  { id: 3, name: "Na provjeri", orderIndex: 3 },
  { id: 4, name: "Zatvoren", orderIndex: 4 },
];

export const users: User[] = [
  {
    id: 1,
    username: "ihorvat",
    firstName: "Ivan",
    lastName: "Horvat",
    email: "ivan.horvat@techsolutions.hr",
    roleId: 1,
  },
  {
    id: 2,
    username: "akovacevic",
    firstName: "Ana",
    lastName: "Kovačević",
    email: "ana.kovacevic@techsolutions.hr",
    roleId: 2,
  },
  {
    id: 3,
    username: "mbabic",
    firstName: "Marko",
    lastName: "Babić",
    email: "marko.babic@techsolutions.hr",
    roleId: 2,
  },
  {
    id: 4,
    username: "pnovak",
    firstName: "Petra",
    lastName: "Novak",
    email: "petra.novak@techsolutions.hr",
    roleId: 3,
  },
  {
    id: 5,
    username: "ljuric",
    firstName: "Luka",
    lastName: "Jurić",
    email: "luka.juric@techsolutions.hr",
    roleId: 3,
  },
  {
    id: 6,
    username: "mtomic",
    firstName: "Maja",
    lastName: "Tomić",
    email: "maja.tomic@techsolutions.hr",
    roleId: 3,
  },
];

export const initialProjects: Project[] = [
  {
    id: 1,
    name: "Web trgovina",
    description:
      "Razvoj web trgovine za klijenta XY d.o.o. Uključuje frontend, backend i integraciju s platnim sustavom.",
    startDate: "2026-01-15",
    endDate: "2026-06-30",
    statusId: 1,
    managerId: 2,
    memberIds: [2, 4, 5],
    createdAt: "2026-01-10T09:00:00Z",
  },
  {
    id: 2,
    name: "Mobilna aplikacija",
    description:
      "Razvoj mobilne aplikacije za internu uporabu. iOS i Android verzija s push notifikacijama.",
    startDate: "2026-03-01",
    endDate: "2026-09-30",
    statusId: 1,
    managerId: 3,
    memberIds: [3, 5, 6],
    createdAt: "2026-02-20T09:00:00Z",
  },
];

export const initialTasks: Task[] = [
  {
    id: 1,
    name: "Dizajn baze podataka",
    description:
      "Kreirati ER dijagram i DDL skriptu za bazu podataka web trgovine.",
    priorityId: 3,
    statusId: 4,
    projectId: 1,
    assigneeId: 4,
    creatorId: 2,
    deadline: "2026-02-15",
    createdAt: "2026-01-16T09:00:00Z",
  },
  {
    id: 2,
    name: "Implementacija REST API-ja",
    description:
      "Razviti backend API za upravljanje proizvodima, košaricom i narudžbama.",
    priorityId: 3,
    statusId: 2,
    projectId: 1,
    assigneeId: 5,
    creatorId: 2,
    deadline: "2026-04-15",
    createdAt: "2026-02-01T09:00:00Z",
  },
  {
    id: 3,
    name: "Izrada wireframea",
    description:
      "Dizajnirati wireframe za glavne stranice: početna, katalog, košarica, checkout.",
    priorityId: 2,
    statusId: 3,
    projectId: 1,
    assigneeId: 4,
    creatorId: 2,
    deadline: "2026-03-01",
    createdAt: "2026-02-01T09:30:00Z",
  },
  {
    id: 4,
    name: "Postavljanje CI/CD pipeline-a",
    description:
      "Konfigurirati GitHub Actions za automatski build i deploy na testni server.",
    priorityId: 2,
    statusId: 1,
    projectId: 2,
    assigneeId: 6,
    creatorId: 3,
    deadline: "2026-04-15",
    createdAt: "2026-03-05T09:00:00Z",
  },
  {
    id: 5,
    name: "Razvoj korisničkog sučelja",
    description:
      "Implementirati ekrane za prijavu, dashboard i listu obavijesti.",
    priorityId: 4,
    statusId: 2,
    projectId: 2,
    assigneeId: 5,
    creatorId: 3,
    deadline: "2026-05-30",
    createdAt: "2026-03-05T09:00:00Z",
  },
  {
    id: 6,
    name: "Pisanje tehničke dokumentacije",
    description:
      "Dokumentirati API endpointe, arhitekturu i upute za deployment.",
    priorityId: 1,
    statusId: 1,
    projectId: 2,
    assigneeId: null,
    creatorId: 3,
    deadline: "2026-08-30",
    createdAt: "2026-03-05T09:00:00Z",
  },
];

export const initialComments: Comment[] = [
  {
    id: 1,
    text: "Kickoff sastanak je u petak u 10h. Molim sve članove tima da pripreme statuse svojih zadataka.",
    projectId: 1,
    taskId: null,
    authorId: 2,
    createdAt: "2026-01-12T08:30:00Z",
  },
  {
    id: 2,
    text: "Dodala sam Petru i Luku na projekt — Petra je lead za dizajn, Luka za backend.",
    projectId: 1,
    taskId: null,
    authorId: 2,
    createdAt: "2026-01-13T10:15:00Z",
  },
  {
    id: 3,
    text: "Prvi prototip mobilne aplikacije bit će dostupan u sljedeća 2 tjedna. Maja preuzima CI/CD postavke.",
    projectId: 2,
    taskId: null,
    authorId: 3,
    createdAt: "2026-03-02T11:00:00Z",
  },
];
