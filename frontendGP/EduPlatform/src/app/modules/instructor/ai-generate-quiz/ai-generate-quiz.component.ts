import { Component } from '@angular/core';
import { CreateQuizComponent } from '../create-quiz/create-quiz.component';
import { CreateQuizService } from 'src/app/services/creat-quiz-service/create-quiz.service';
import { SpinnerComponent } from 'src/app/utils/spinner/spinner.component';
import { Router } from '@angular/router';
import { Quiz } from 'src/app/models/Quiz';
import { QuestionForInstructor } from 'src/app/models/questionForInstructor';

@Component({
  selector: 'app-ai-generate-quiz',
  templateUrl: './ai-generate-quiz.component.html',
  styleUrls: ['./ai-generate-quiz.component.scss']
})
export class AiGenerateQuizComponent {
saveQuestions() {
throw new Error('Method not implemented.');
}

  constructor(private quizService: CreateQuizService, private router: Router) {}
  quiz: Quiz | null = null;
  loading: boolean = false;
  inputType: string = 'pdf';
  textInput: string = '';
  selectedFiles: File[] = [];
  questions: QuestionForInstructor[] = [];
  file: File | null = null;

  ngOnInit() {
    this.quiz = this.quizService.getQuiz();
    if (!this.quiz) {
      console.error('No quiz data found!');
      this.router.navigate(['/create-quiz']);
    }
  }
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
      this.submitQuiz();
      
    } else if (this.inputType === 'pdf' && this.file) {
      this.submitQuiz();
    }
  }
  submitQuiz() {

    this.loading = true;
  
    this.quizService.submitQuiz(this.selectedFiles).subscribe(response => {
      
      console.log('Quiz submitted successfully:', response);
      console
      this.quizService.setQuizId(response.quizId);
      this.getQuiz();
      // Handle the response here
    }, error => {
      console.error('Error submitting quiz:', error);
      // Handle the error here
    });
  }
  getQuiz() {
    this.quizService.getQuizForInstructor().subscribe(response => {
      console.log(response)
      this.questions=response.questions;
      this.loading = false;

    },error => {
      console.error('Error getting quiz:', error);
    }
  )
    
  }

  toggleQuestion(index: number) {
    this.questions[index].expanded = !this.questions[index].expanded;
  }

  // addNewQuestion() {
  //   this.questions.push({
  //     title: 'New Question',
  //     content: '',
  //     expanded: true,
  //     answers: [
  //       { text: 'Answer 1', isCorrect: false },
  //       { text: 'Answer 2', isCorrect: false },
  //       { text: 'Answer 3', isCorrect: false },
  //       { text: 'Answer 4', isCorrect: false }
  //     ]
  //   });
  // }

  saveQuestion(index: number) {
    console.log(`Question ${index + 1} saved`);
  }
  deleteQuestion(_t40: number) {
    throw new Error('Method not implemented.');
  }
}
