import { Answer } from "./answer";

export interface QuestionForInstructor {
    questionId: number;
    text: string;
    points: number;
    expanded: boolean ;
    answers: Answer[];

  }
  