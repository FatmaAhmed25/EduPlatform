import { Component } from '@angular/core';

@Component({
  selector: 'app-ai-generate-quiz',
  templateUrl: './ai-generate-quiz.component.html',
  styleUrls: ['./ai-generate-quiz.component.scss']
})
export class AiGenerateQuizComponent {
deleteQuestion(_t40: number) {
throw new Error('Method not implemented.');
}
  inputType: string = 'pdf';
  textInput: string = '';
  selectedFiles: File[] = [];
  questions: any[] = [
    {
      title: 'Sample Question 1',
      content: 'What is Angular?',
      expanded: false,
      answers: [
        { text: 'A framework', isCorrect: true },
        { text: 'A library', isCorrect: false },
        { text: 'A language', isCorrect: false },
        { text: 'A plugin', isCorrect: false }
      ]
    },
    {
      title: 'Sample Question 2',
      content: 'Describe the MVC architecture.',
      expanded: false,
      answers: [
        { text: 'Model, View, Controller', isCorrect: true },
        { text: 'Model, View, Component', isCorrect: false },
        { text: 'Module, View, Controller', isCorrect: false },
        { text: 'Module, View, Component', isCorrect: false }
      ]
    }
  ];
  file: File | null = null;

  onFilesSelected(event: any) {
    //this.file = event.target.files[0];
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const newFiles = Array.from(input.files);
      this.selectedFiles = this.selectedFiles.concat(newFiles);
      // Remove duplicates if necessary
      this.selectedFiles = Array.from(new Set(this.selectedFiles));
    }
  }
  removeFile(index: number) {
    this.selectedFiles.splice(index, 1);
  }

  generateQuestions() {
    if (this.inputType === 'text' && this.textInput) {
      this.questions.push({
        title: 'AI-Generated Question',
        content: this.textInput,
        expanded: false,
        answers: [
          { text: 'Answer 1', isCorrect: false },
          { text: 'Answer 2', isCorrect: false },
          { text: 'Answer 3', isCorrect: false },
          { text: 'Answer 4', isCorrect: false }
        ]
      });
    } else if (this.inputType === 'pdf' && this.file) {
      this.questions.push({
        title: 'AI-Generated Question from PDF',
        content: `Questions generated from: ${this.file.name}`,
        expanded: false,
        answers: [
          { text: 'Answer 1', isCorrect: false },
          { text: 'Answer 2', isCorrect: false },
          { text: 'Answer 3', isCorrect: false },
          { text: 'Answer 4', isCorrect: false }
        ]
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
      expanded: true,
      answers: [
        { text: 'Answer 1', isCorrect: false },
        { text: 'Answer 2', isCorrect: false },
        { text: 'Answer 3', isCorrect: false },
        { text: 'Answer 4', isCorrect: false }
      ]
    });
  }

  saveQuestion(index: number) {
    console.log(`Question ${index + 1} saved`);
  }
}
