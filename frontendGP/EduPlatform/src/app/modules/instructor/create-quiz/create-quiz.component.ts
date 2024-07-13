import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CreateQuizService } from 'src/app/services/creat-quiz-service/create-quiz.service';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-create-quiz',
  templateUrl: './create-quiz.component.html',
  styleUrls: ['./create-quiz.component.scss'],
  animations: [
    trigger('formAnimations', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class CreateQuizComponent implements OnInit {
  assessmentTypes = [
    { value: 'ai-generated-mcq', label: 'AI Generated MCQ Quiz' },
    { value: 'ai-generated-essay', label: 'AI Generated Essay Quiz' },
    { value: 'manual-mcq', label: 'Manually Created MCQ Quiz' },
    { value: 'manual-essay', label: 'Manually Created Essay Quiz' }
  ];

  courses: any[] = [];
  quizForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private http: HttpClient,
    private quizService: CreateQuizService
  ) {
    this.quizForm = this.fb.group({
      typeOfAssessment: ['', Validators.required],
      assessmentName: ['', Validators.required],
      startDate: ['', Validators.required],
      startTime: ['', Validators.required],
      endDate: ['', Validators.required],
      endTime: ['', Validators.required],
      numOfQuestions: ['', [Validators.required, Validators.min(1), Validators.max(100)]],
      selectedCourse: ['', Validators.required]
    });
  }

  ngOnInit() {
    const userId = localStorage.getItem('userID');
    if (userId) {
      this.quizService.getCoursesForInstructor(userId).subscribe(courses => {
        if (courses.error) {
          console.error(courses.error);
        } else {
          this.courses = courses;
        }
      });
    } else {
      console.error('User ID is not available.');
    }
  }

  navigateToGenerateQuiz() {
    if (this.quizForm.invalid) {
      this.quizForm.markAllAsTouched();
      return;
    }

    const startDate = this.quizForm.get('startDate')?.value;
    const startTime = this.quizForm.get('startTime')?.value;
    const endDate = this.quizForm.get('endDate')?.value;
    const endTime = this.quizForm.get('endTime')?.value;
    const selectedCourse = this.quizForm.get('selectedCourse')?.value;

    const quizData = {
      typeOfAssessment: this.quizForm.get('typeOfAssessment')?.value,
      assessmentName: this.quizForm.get('assessmentName')?.value,
      startDate: new Date(`${startDate}T${startTime}`).toISOString(),
      endDate: new Date(`${endDate}T${endTime}`).toISOString(),
      numOfQuestions: this.quizForm.get('numOfQuestions')?.value,
      courseName: selectedCourse.title,
      courseId: selectedCourse.courseId,
      instructorId: localStorage.getItem('userID')!,
    };

    this.quizService.setQuiz(quizData);

    if (quizData.typeOfAssessment === 'manual-mcq') {
      this.router.navigate(['/manual-mcq-quiz']);
    } else if (quizData.typeOfAssessment === 'manual-essay') {
      this.router.navigate(['/manual-essay-quiz']);
    } else {
      this.router.navigate(['/ai-generate-quiz']);
    }
  }

  setTypeOfAssessment(type: any) {
    this.quizForm.patchValue({ typeOfAssessment: type.value });
    this.closeDropdown('t1');

  }

  setSelectedCourse(course: any) {
    this.quizForm.patchValue({ selectedCourse: course });
    this.closeDropdown('t2');

  }

  closeDropdown(id: string) {
    const checkbox = document.getElementById(id) as HTMLInputElement;
    checkbox.checked = false;
  }
}
