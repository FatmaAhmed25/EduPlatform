export interface Course {
    courseId: number;
    courseCode: string;
    title: string;
    description: string;
    password: string;
    assignments: any[];
    image?: string; // Add this line

  }
  