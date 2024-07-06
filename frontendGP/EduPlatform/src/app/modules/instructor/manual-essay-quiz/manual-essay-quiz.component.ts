import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { ManualQuizService } from 'src/app/services/manual-quizService/manual-quiz.service';
import { CreateQuizComponent } from '../create-quiz/create-quiz.component';
import { CreateQuizService } from 'src/app/services/creat-quiz-service/create-quiz.service';
import { LoginComponent } from '../../login/login.component';

@Component({
  selector: 'app-manual-essay-quiz',
  templateUrl: './manual-essay-quiz.component.html',
  styleUrls: ['./manual-essay-quiz.component.css']
})
export class ManualEssayQuizComponent implements OnInit {
  quizForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private quizService: CreateQuizService,
    private auth: LoginComponent,
  ) {}

  ngOnInit(): void {
    this.quizForm = this.fb.group({
      totalGrade: ['', Validators.required],
      questions: this.fb.array([])
    });
  }

  get questions(): FormArray {
    return this.quizForm.get('questions') as FormArray;
  }

  addQuestion(): void {
    // this.auth.isLoggedIn();
    this.questions.push(this.fb.group({
      text: ['', Validators.required],
      points: ['', Validators.required],
      questionType:['ESSAY']
    }));
  }

  removeQuestion(index: number): void {
    this.questions.removeAt(index);
  }

  submitQuiz(): void {
    if (this.quizForm.valid) {
      const quizData = {
        ...this.quizForm.value,
        assessmentName: this.quizService.getQuiz()?.assessmentName,
        startDate: this.quizService.getQuiz()?.startDate,
        endDate: this.quizService.getQuiz()?.endDate,
      };
      
      this.quizService.createManualQuiz(quizData).subscribe(
        response => {
          const quizId=response.quizId;
          this.router.navigate(['/mcq-quiz-viewer',quizId]);
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
