import { Component } from '@angular/core';

@Component({
  selector: 'app-ai-generate-quiz',
  templateUrl: './ai-generate-quiz.component.html',
  styleUrls: ['./ai-generate-quiz.component.scss']
})
export class AiGenerateQuizComponent {
  inputType: string = 'text';
  textInput: string = '';
  questions: any[] = [];
  file: File | null = null;

  onFileSelected(event: any) {
    this.file = event.target.files[0];
  }

  generateQuestions() {
    // Example logic for generating questions (to be replaced with actual AI integration)
    if (this.inputType === 'text' && this.textInput) {
      this.questions.push({
        title: 'AI-Generated Question',
        content: this.textInput,
        expanded: false
      });
    } else if (this.inputType === 'pdf' && this.file) {
      this.questions.push({
        title: 'AI-Generated Question from PDF',
        content: `Questions generated from: ${this.file.name}`,
        expanded: false
      });
    }
  }

  toggleQuestion(index: number) {
    this.questions[index].expanded = !this.questions[index].expanded;
  }

  addNewQuestion() {
    this.questions.push({
      title: 'New Question',
      content: '',
      expanded: true
    });
  }
}
