export interface QuizSubmissionDTO {
    courseId: number;
    quizId: number;
    courseName: string;
    quizTitle: string;
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