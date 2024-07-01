import { Component } from '@angular/core';
import { CreateQuizComponent } from '../create-quiz/create-quiz.component';
import { CreateQuizService } from 'src/app/services/quiz-service/create-quiz.service';
import { SpinnerComponent } from 'src/app/utils/spinner/spinner.component';

@Component({
  selector: 'app-ai-generate-quiz',
  templateUrl: './ai-generate-quiz.component.html',
  styleUrls: ['./ai-generate-quiz.component.scss']
})
export class AiGenerateQuizComponent {

  constructor(private quizService: CreateQuizService) {}
deleteQuestion(_t40: number) {
throw new Error('Method not implemented.');
}
  loading: boolean = false;
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
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const newFiles = Array.from(input.files);
  
      // Define allowed file types and extensions for PDFs
      const allowedType = 'application/pdf';
      const allowedExtension = '.pdf';
  
      // Store invalid files for feedback
      const invalidFiles: string[] = [];
  
      // Filter files to include only PDFs
      const filteredFiles = newFiles.filter(file => {
        const isPDF = file.type === allowedType && file.name.toLowerCase().endsWith(allowedExtension);
        if (!isPDF) {
          invalidFiles.push(file.name);
        }
        return isPDF;
      });
  
      // Add valid files to the selectedFiles list
      this.selectedFiles = [...this.selectedFiles, ...filteredFiles];
  
      // Remove duplicates if necessary
      this.selectedFiles = Array.from(new Set(this.selectedFiles.map(file => file.name)))
                               .map(name => this.selectedFiles.find(file => file.name === name) as File);
  
      // Alert the user about invalid files
      if (invalidFiles.length > 0) {
        alert(`The following files are not allowed and were ignored: ${invalidFiles.join(', ')}. Please upload PDF files only.`);
      }
    }
  }
  
  
  removeFile(index: number) {
    this.selectedFiles.splice(index, 1);
  }

  generateQuestions() {
    console.log('Generating')
    this.submitQuiz();
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
      // this.questions.push({
      //   title: 'AI-Generated Question from PDF',
      //   content: `Questions generated from: ${this.file.name}`,
      //   expanded: false,
      //   answers: [
      //     { text: 'Answer 1', isCorrect: false },
      //     { text: 'Answer 2', isCorrect: false },
      //     { text: 'Answer 3', isCorrect: false },
      //     { text: 'Answer 4', isCorrect: false }
      //   ]
      // });
      this.submitQuiz();
    }
  }
  submitQuiz() {
    this.loading = true;
    console.log("here")
    const formData = new FormData();

    this.selectedFiles.forEach(file => {
      formData.append('pdf_files', file, file.name);
    });

    formData.append('courseId', '1');
    formData.append('quiz_title', 'DS Quiz');
    formData.append('startTime', '2024-06-23T07:25:24.952Z');
    formData.append('endTime', '2024-06-23T07:25:24.952Z');

    this.quizService.submitQuiz(formData).subscribe(response => {
      
      console.log('Quiz submitted successfully:', response);
      // Handle the response here
    }, error => {
      console.error('Error submitting quiz:', error);
      // Handle the error here
    });
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
