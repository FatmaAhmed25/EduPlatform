import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CreateQuizService } from 'src/app/services/creat-quiz-service/create-quiz.service';
import { Quiz } from 'src/app/models/Quiz';

@Component({
  selector: 'app-manual-mcq-quiz',
  templateUrl: './manual-mcq-quiz.component.html',
  styleUrls: ['./manual-mcq-quiz.component.css']
})
export class ManualMcqQuizComponent implements OnInit {
  quizForm!: FormGroup;
  quiz: Quiz | null = null;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private quizService: CreateQuizService
  ) {}

  ngOnInit(): void {
    this.quiz = this.quizService.getQuiz();
    if (!this.quiz) {
      console.error('No quiz data found!');
      this.router.navigate(['/create-quiz']);
    }
    this.quizForm = this.fb.group({
      totalGrade: ['', Validators.required],
      questions: this.fb.array([])
    });
  }

  get questions(): FormArray {
    return this.quizForm.get('questions') as FormArray;
  }

  getAnswers(questionIndex: number): FormArray {
    return this.questions.at(questionIndex).get('answers') as FormArray;
  }

  addQuestion(): void {
    this.questions.push(this.fb.group({
      text: ['', Validators.required],
      points: ['', Validators.required],
      questionType: ['MCQ', Validators.required],
      answers: this.fb.array([
        this.fb.group({
          text: ['', Validators.required],
          correct: [false]
        })
      ])
    }));
  }

  addAnswer(questionIndex: number): void {
    this.getAnswers(questionIndex).push(this.fb.group({
      text: ['', Validators.required],
      correct: [false]
    }));
  }

  removeQuestion(index: number): void {
    this.questions.removeAt(index);
  }

  removeAnswer(questionIndex: number, answerIndex: number): void {
    this.getAnswers(questionIndex).removeAt(answerIndex);
  }

  toggleCorrectAnswer(questionIndex: number, answerIndex: number): void {
    const answers = this.getAnswers(questionIndex);
    answers.controls.forEach((control, idx) => {
      const correctControl = control.get('correct');
      if (correctControl) {
        correctControl.setValue(idx === answerIndex);
      }
    });
  }

  submitQuiz(): void {
    if (this.quizForm.valid) {
      const quizData = {
        ...this.quizForm.value,
        assessmentName: this.quizService.getQuiz()?.assessmentName,
        startDate: this.quizService.getQuiz()?.startDate,
        endDate: this.quizService.getQuiz()?.endDate,
      };

      this.quizService.createManualMCQQuiz(quizData).subscribe(
        response => {
          const quizId = response.quizId;
          console.log(quizId);
          this.router.navigate(['/instructor/quiz-viewer', quizId]);
        },
        error => {
          console.error('Error submitting quiz:', error);
        }
      );
    } else {
      console.error('Form is invalid');
    }
  }
}
