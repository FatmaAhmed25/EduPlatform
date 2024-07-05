import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Question } from 'src/app/models/question';
import { Quiz } from 'src/app/models/Quiz';
import { CreateQuizService } from 'src/app/services/creat-quiz-service/create-quiz.service';
import { EditQuizService } from 'src/app/services/editQuizService/edit-quiz.service';

@Component({
  selector: 'app-mcq-quiz-viewer-instructor',
  templateUrl: './mcq-quiz-viewer-instructor.component.html',
  styleUrls: ['./mcq-quiz-viewer-instructor.component.scss']
})
export class McqQuizViewerInstructorComponent {


  constructor(private quizService: CreateQuizService,private route: ActivatedRoute, private router: Router, private editQuizService: EditQuizService) {}
  quiz: Quiz | null = null;
  questions: Question[] = [];
  validationErrors: string[] = [];
  quizId: any = null;
  quizTitle=null;
  instructorId: any;
  quizDuration: any;
  quizStartTime: any;

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.quizId = +params['quizId'];
    });
    this.getQuiz();
  }
 
  getQuiz() {
    this.quizService.getQuizForInstructor(this.quizId).subscribe(
      response => {
        console.log(response);
        this.quiz = response;
        this.quizTitle = response.title;
        
        // Parsing start and end time
        const startDate = new Date(response.startTime);
        const endDate = new Date(response.endTime);
        
        // Calculating duration in minutes
        const durationInMilliseconds = endDate.getTime() - startDate.getTime();
        this.quizDuration = Math.round(durationInMilliseconds / (1000 * 60)); // Convert milliseconds to minutes
        
        // Formatting start date to a more readable format
        this.quizStartTime = startDate.toLocaleDateString('en-US', { 
          year: 'numeric', 
          month: 'long', 
          day: 'numeric', 
          hour: '2-digit', 
          minute: '2-digit', 
          second: '2-digit' 
        });
        
        this.questions = response.questions;
      },
      error => {
        console.error('Error getting quiz:', error);
      }
    );
  }
  

  toggleQuestion(index: number) {
    this.questions[index].expanded = !this.questions[index].expanded;
  }

  saveQuestions() {
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
