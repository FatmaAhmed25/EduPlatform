export interface UpcomingQuizDTO {
    courseId:any;
    quizId: number;
    title: string;
    startTime: Date;
    endTime: Date;
    totalGrade: number;
    canTakeQuiz: boolean;
  }