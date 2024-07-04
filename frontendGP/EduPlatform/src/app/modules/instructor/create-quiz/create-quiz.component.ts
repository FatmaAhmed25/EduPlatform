import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Quiz } from 'src/app/models/Quiz';
import { CreateQuizService } from 'src/app/services/quiz-service/create-quiz.service';

@Component({
  selector: 'app-create-quiz',
  templateUrl: './create-quiz.component.html',
  styleUrls: ['./create-quiz.component.scss']
})
export class CreateQuizComponent {
  assessmentTypes = [
    { value: 'ai-generated-mcq', label: 'AI Generated MCQ Quiz' },
    { value: 'ai-generated-essay', label: 'AI Generated Essay Quiz' },
    { value: 'manual-mcq', label: 'Manually Created MCQ Quiz' },
    { value: 'manual-essay', label: 'Manually Created Essay Quiz' }
  ];
  quizForm: FormGroup;

  constructor(private fb: FormBuilder, private router: Router, private quizService: CreateQuizService) {
    this.quizForm = this.fb.group({
      typeOfAssessment: ['', Validators.required],
      assessmentName: ['', Validators.required],
      startDate: ['', Validators.required],
      startTime: ['', Validators.required],
      endDate: ['', Validators.required],
      endTime: ['', Validators.required],
      numOfQuestions: ['', [Validators.required, Validators.min(1), Validators.max(100)]]
    });
  }
  navigateToGenerateQuiz() {
    console.log(this.quizForm);
    if (this.quizForm.invalid) {
      // Mark all controls as touched to trigger validation messages
      this.quizForm.markAllAsTouched();
      return;
    }

    const startDate = this.quizForm.get('startDate')?.value;
    const startTime = this.quizForm.get('startTime')?.value;
    const endDate = this.quizForm.get('endDate')?.value;
    const endTime = this.quizForm.get('endTime')?.value;

    const quiz: Quiz = {
      typeOfAssessment: this.quizForm.get('typeOfAssessment')?.value,
      assessmentName: this.quizForm.get('assessmentName')?.value,
      startDate: new Date(`${startDate}T${startTime}`).toISOString(), // Combine date and time
      endDate: new Date(`${endDate}T${endTime}`).toISOString(), // Combine date and time
      numOfQuestions: this.quizForm.get('numOfQuestions')?.value,
      courseName: 'Sarsour quiz',
      courseId: '1',
      instructorId:'7' 
    };

    this.quizService.setQuiz(quiz);
    this.router.navigate(['/ai-generate-quiz']);
  }
}

