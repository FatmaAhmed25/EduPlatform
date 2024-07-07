import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { StudentQuizService } from 'src/app/services/Student-quiz-service/student-quiz.service';
import { interval, Subscription } from 'rxjs';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmQuizSubmissionDialogComponent } from 'src/app/dialogs/confirm-quiz-submition-dialog/confirm-quiz-submission-dialog/confirm-quiz-submission-dialog.component';
import { ActivatedRoute, Router } from '@angular/router';

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
  quizId: any;
  courseId: any;
  cheatingStatus: string = 'PASSED';

  constructor(private router: Router, private quizService: StudentQuizService, private datePipe: DatePipe, private dialog: MatDialog, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.quizId = +params['quizId'];
      this.courseId = +params['courseId'];
    });
    this.quizService.getQuiz(this.courseId, this.quizId).subscribe(
      (data: any) => {
        this.quiz = data;
        this.initializeAnsweredQuestions();
        this.startCountdown();
        this.requestFullscreen();
        console.log(data);
      },
      (error) => {
        console.error('Error fetching quiz:', error);
        this.errorMessage = error;
      }
    );
  }

  ngOnDestroy(): void {
    // window.removeEventListener('blur', this.onWindowBlur);
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
    }
  }

  requestFullscreen(): void {
    const docElement = document.documentElement as any;
    if (docElement.requestFullscreen) {
      docElement.requestFullscreen();
    } else if (docElement.webkitRequestFullscreen) {
      docElement.webkitRequestFullscreen();
    } else if (docElement.mozRequestFullScreen) {
      docElement.mozRequestFullScreen();
    } else if (docElement.msRequestFullscreen) {
      docElement.msRequestFullscreen();
    }
  }

  @HostListener('document:fullscreenchange')
  @HostListener('document:webkitfullscreenchange')
  @HostListener('document:mozfullscreenchange')
  @HostListener('document:MSFullscreenChange')
  onFullScreenChange(): void {
    if (!document.fullscreenElement) {
      this.cheatingStatus = 'CHEATING';
      this.submitQuiz();
      this.router.navigate(['/upcoming-quizzes']);
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

  @HostListener('window:resize', ['$event'])
  onResize(event: any): void {}

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
          this.submitQuiz();
          this.router.navigate(['/take-quiz-error']);
        } else {
          const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
          const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
          const seconds = Math.floor((distance % (1000 * 60)) / 1000);

          this.countdown = `${hours}h ${minutes}m ${seconds}s`;
        }
      });
    }
  }

  saveEssayAnswer(answer: string, index: number): void {
    if (this.quiz) {
      this.answeredQuestions[index] = true;
      this.selectedAnswers[index] = answer;
    }
  }

  hasNextQuestion(): boolean {
    return this.currentQuestionIndex < ((this.quiz?.questions?.length ?? 0) - 1);
  }

  confirmSubmition() {
    const dialogRef = this.dialog.open(ConfirmQuizSubmissionDialogComponent);

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.submitQuiz();
      } else {
        console.log('Quiz submission cancelled.');
      }
    });
  }

  submitMcqQuiz(): void {
    if (this.quiz && this.quizId) {
      const studentId = localStorage.getItem('userID');
      console.log('student id: ' + studentId);
      if (studentId) {
        const payload = {
          studentId: studentId,
          quizId: this.quizId,
          answers: this.quiz.questions.map((question, index) => ({
            questionId: question.questionId,
            answerId: this.selectedAnswers[index]
          })),
          cheatingStatus: this.cheatingStatus
        };
        this.quizService.submitMCQQuiz(payload).subscribe(
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

  submitEssayQuiz(): void {
    if (this.quiz && this.quizId) {
      const studentId = Number(localStorage.getItem('userID'));
      console.log('student id: ' + studentId);
      console.log('quiz id: ' + this.quizId);
      if (studentId) {
        const payload = {
          studentId: studentId,
          quizId: this.quizId,
          answers: this.quiz.questions.map((question, index) => ({
            questionId: question.questionId,
            answer: this.selectedAnswers[index]
          })),
          cheatingStatus: this.cheatingStatus
        };
        console.log(payload);
        this.quizService.submitEssayQuiz(payload).subscribe(
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

  handleCheatingDetected(event: string): void {
    this.cheatingStatus = event;
    console.log('Cheating status updated:', this.cheatingStatus);
  }

  submitQuiz(): void {
    if (this.quiz) {
      const firstQuestion = this.quiz.questions[0];
      if (firstQuestion.questionType === 'MCQ') {
        this.submitMcqQuiz();
      } else {
        this.submitEssayQuiz();
      }
      this.router.navigate(['/upcoming-quizzes']);
    }
  }
}
