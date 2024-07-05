import { Component, OnInit, OnDestroy } from '@angular/core';
import { StudentQuizService } from 'src/app/services/Student-quiz-service/student-quiz.service';
import { interval, Subscription } from 'rxjs';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmQuizSubmissionDialogComponent } from 'src/app/dialogs/confirm-quiz-submition-dialog/confirm-quiz-submission-dialog/confirm-quiz-submission-dialog.component';

interface Answer {
  answerId: string;
  text: string;
}

interface Question {
  questionId: any;
  text: string;
  points: number;
  questionType: string;
  answers: Answer[];
}

interface Quiz {
  quizId: number;
  title: string;
  startTime: string;
  endTime: string;
  totalGrade: number;
  courseId: number;
  questions: Question[];
}

@Component({
  selector: 'app-quiz-details',
  templateUrl: './quiz-details.component.html',
  styleUrls: ['./quiz-details.component.scss'],
  providers: [DatePipe]
})
export class QuizDetailsComponent implements OnInit, OnDestroy {
  quiz: Quiz | undefined;
  currentQuestionIndex: number = 0;
  selectedQuestionIndex: number | null = null;
  answeredQuestions: boolean[] = [];
  selectedAnswers: (string | null)[] = [];
  countdown: string = '';
  timerSubscription: Subscription | null = null;
  errorMessage: string | null = null;


  constructor(private quizService: StudentQuizService, private datePipe: DatePipe,private dialog: MatDialog) {}

  ngOnInit(): void {
    this.quizService.getQuiz().subscribe(
      (data: any) => {
        this.quiz = data;
        this.initializeAnsweredQuestions();
        this.startCountdown();
        console.log(data)
      },
      (error) => {
        console.error('Error fetching quiz:', error);
        this.errorMessage = error;
      }
    );
  }

  ngOnDestroy(): void {
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
    }
  }

  initializeAnsweredQuestions(): void {
    if (this.quiz) {
      this.answeredQuestions = new Array(this.quiz.questions.length).fill(false);
      this.selectedAnswers = new Array(this.quiz.questions.length).fill(null);
    }
  }

  get currentQuestion(): Question | undefined {
    return this.quiz?.questions?.[this.currentQuestionIndex];
  }

  selectQuestion(index: number): void {
    this.selectedQuestionIndex = index;
    this.currentQuestionIndex = index;
  }

  markQuestionAsCompleted(selectedAnswer: string): void {
    if (this.quiz) {
      this.answeredQuestions[this.currentQuestionIndex] = true;
      this.selectedAnswers[this.currentQuestionIndex] = selectedAnswer;
    }
  }

  goToNextQuestion(): void {
    if (this.quiz && this.currentQuestionIndex < this.quiz.questions.length - 1) {
      this.selectQuestion(this.currentQuestionIndex + 1);
    }
  }

  getAnsweredQuestionsCount(): number {
    return this.answeredQuestions.filter(Boolean).length;
  }

  startCountdown(): void {
    if (this.quiz) {
      const endTimeISO = this.quiz.endTime;
      const endTime = new Date(endTimeISO);
      const endTimeMilliseconds = endTime.getTime();

      this.timerSubscription = interval(1000).subscribe(() => {
        const now = new Date().getTime();
        const distance = endTimeMilliseconds - now;

        if (distance <= 0) {
          this.countdown = 'Time is up!';
          this.timerSubscription?.unsubscribe();
        } else {
          const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
          const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
          const seconds = Math.floor((distance % (1000 * 60)) / 1000);

          this.countdown = `${hours}h ${minutes}m ${seconds}s`;
        }
      });
    }
  }

  confirmSubmition(){
    const dialogRef = this.dialog.open(ConfirmQuizSubmissionDialogComponent);

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.submitQuiz();
      }
      else {
        // User clicked Cancel or clicked outside the dialog
        console.log('Quiz submission cancelled.');
      }
    });
  }

  submitQuiz(): void {
    if (this.quiz) {
      const studentId = localStorage.getItem('userID')
      const quizId = 2; 
      console.log('student id: ' + studentId);
      console.log('quiz id: ' + quizId);
      if(studentId && quizId) {
        const payload = {
          studentId: studentId,
          quizId: quizId,
          answers: this.quiz.questions.map((question, index) => ({
            questionId: question.questionId,
            answerId: this.selectedAnswers[index]
          }))
        };
        this.quizService.submitQuiz(payload).subscribe(
        (response) => {
          console.log('Quiz submitted successfully:', response);
        },
        (error) => {
          console.error('Error submitting quiz:', error);
        }
      );
    }
    }

    
      
  }
}
