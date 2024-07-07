export interface instructorQuizSubmissionDTO {
    studentId: number;
    courseId: number;
    quizId: number;
    studentName: string;
    quizSubmission: {
      id: number;
      submissionTime: string;
      totalGrade: number;
      cheatingStatus: string;
      cheatingReport: {
        id: number;
        folderName: string;
      };
    };
  }