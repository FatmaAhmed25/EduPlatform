import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { ManualQuizService } from 'src/app/services/manual-quizService/manual-quiz.service';

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
    private manualQuizService: ManualQuizService
  ) {}

  ngOnInit(): void {
    this.quizForm = this.fb.group({
      title: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      totalGrade: ['', Validators.required],
      courseId: ['', Validators.required],
      questions: this.fb.array([])
    });
  }

  get questions(): FormArray {
    return this.quizForm.get('questions') as FormArray;
  }

  addQuestion(): void {
    this.questions.push(this.fb.group({
      text: ['', Validators.required],
      points: ['', Validators.required],
    }));
  }

  removeQuestion(index: number): void {
    this.questions.removeAt(index);
  }

  submitQuiz(): void {
    if (this.quizForm.valid) {
      const quizData = this.quizForm.value;
      this.manualQuizService.createQuiz(quizData).subscribe(() => {
        this.router.navigate(['/quiz-list']);
      });
    } else {
      console.error('Form is invalid');
    }
  }
}
