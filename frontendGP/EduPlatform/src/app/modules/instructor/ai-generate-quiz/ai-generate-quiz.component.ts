import { Component } from '@angular/core';
import { CreateQuizComponent } from '../create-quiz/create-quiz.component';
import { CreateQuizService } from 'src/app/services/creat-quiz-service/create-quiz.service';
import { SpinnerComponent } from 'src/app/utils/spinner/spinner.component';
import { Router } from '@angular/router';
import { Quiz } from 'src/app/models/Quiz';
import { Question } from 'src/app/models/question';
import { EditQuizService } from 'src/app/services/editQuizService/edit-quiz.service';

@Component({
  selector: 'app-ai-generate-quiz',
  templateUrl: './ai-generate-quiz.component.html',
  styleUrls: ['./ai-generate-quiz.component.scss']
})

export class AiGenerateQuizComponent {
  instructorId: any;

  constructor(private quizService: CreateQuizService, private router: Router, private editQuizService: EditQuizService) {}
  quiz: Quiz | null = null;
  loading: boolean = false;
  inputType: string = 'pdf';
  textInput: string = '';
  selectedFiles: File[] = [];
  questions: Question[] = [];
  file: File | null = null;
  validationErrors: string[] = [];
  

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

  saveQuestions() {
    // this.validationErrors = [];
    // let allValid = true;
    // this.questions.forEach((question, index) => {
    //   if (question.points <= 0 || isNaN(question.points)) {
    //     this.validationErrors.push(`Question ${index + 1} must have a point value greater than zero.`);
    //     allValid = false;
    //   }
    // });
    // if (allValid && this.quiz) {
    console.log("here");
    const instructorId = localStorage.getItem('userID');
        if(instructorId && this.quiz?.quizId){
          this.editQuizService.updateQuiz(this.quiz.quizId,instructorId,this.quiz).subscribe({
            next: (updatedQuiz) => {
              console.log('Quiz updated successfully:', updatedQuiz);
              this.getQuiz();
            },
            error: (err) => {
              console.error('Error updating quiz:', err);
            }
          });
        }
  // }}
  }
  deleteQuestion(index: number) {
    this.questions.splice(index, 1);
  }
  validatePoints(index: number) {
    const points = this.questions[index].points;
    if (points <= 0 || isNaN(points)) {
      this.validationErrors.push(`Question ${index + 1} must have a point value greater than zero.`);
    }
  }
  }
  
