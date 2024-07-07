import { Student } from "./Student.model";

export interface FileSubmission {
    id: number;
    fileName: string;
    student: Student;
  }