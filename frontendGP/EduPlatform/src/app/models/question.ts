import { Answer } from "./answer";

export interface Question {
    questionId: number;
    text: string;
    points: number;
    expanded: boolean ;
    answers: Answer[];
  }
  