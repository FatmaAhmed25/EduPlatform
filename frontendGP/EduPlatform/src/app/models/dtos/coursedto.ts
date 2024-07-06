export interface CourseDTO {
    id: number;
    title: string;
    numberOfEnrolledStudents: number;
    createdByInstructorName: string;
    tasInstructorNames: string[];
    courseCode: string;
    password: string;
    showPassword?: boolean;
  }
  