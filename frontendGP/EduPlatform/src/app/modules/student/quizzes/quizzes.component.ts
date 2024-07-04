// src/app/components/quiz/quiz.component.ts
import { Component, OnInit, Input } from '@angular/core';
import { QuizService } from 'src/app/services/quizService/quiz.service';

@Component({
  selector: 'app-quiz',
  templateUrl: './quizzes.component.html',
  styleUrls: ['./quizzes.component.css']
})
export class QuizzesComponent implements OnInit {
  @Input() courseId: number | undefined;
  quizzes: any[] = [];
  selectedQuizId: number | null = null;

  constructor(private quizService: QuizService) {}

  ngOnInit(): void {
    if (this.courseId) {
      this.fetchQuizzes();
    }
  }

  fetchQuizzes(): void {
    if (this.courseId) {
      this.quizService.getQuizzes(this.courseId).subscribe(data => {
        this.quizzes = data;
      });
    }
  }

  takeQuiz(quiz: any): void {
    // Logic to start the quiz
    console.log('Taking quiz:', quiz);
  }
}
