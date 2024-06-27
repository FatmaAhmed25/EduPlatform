import { Component } from '@angular/core';
import { Router } from '@angular/router';

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

  constructor(private router: Router) {}

  navigateToGenerateQuiz() {
    this.router.navigate(['/ai-generate-quiz']);
  }
}
