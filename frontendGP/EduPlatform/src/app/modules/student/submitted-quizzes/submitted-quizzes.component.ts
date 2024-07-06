import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { CourseDTO } from 'src/app/models/dtos/coursedto';
import { QuizSubmissionDTO } from 'src/app/models/dtos/quiz-submission-dto';
import { UserDTO } from 'src/app/models/dtos/usersdto';
import { AdminService } from 'src/app/services/admin.service';
import { QuizSubmissionsService } from 'src/app/services/quiz-submission-servie/quiz-submissions.service';

@Component({
  selector: 'app-submitted-quizzes',
  templateUrl: './submitted-quizzes.component.html',
  styleUrls: ['./submitted-quizzes.component.scss']
})
export class SubmittedQuizzesComponent {

  quizzes: QuizSubmissionDTO[] = [];

  constructor(private quizService: QuizSubmissionsService) {}

  ngOnInit(): void {
    this.fetchSubmittedQuizzes();

  }

  fetchSubmittedQuizzes(): void {
    const studentId = this.getUserIdFromLocalStorage();
    this.quizService.getQuizSubmissionsForStudent(studentId).subscribe(
      quizzes => {
        this.quizzes = quizzes;
        console.log('Submitted Quizzes:', this.quizzes);
      },
      error => {
        console.error('Error fetching submitted quizzes:', error);

      }
    );
  }

  togglePasswordVisibility(course: any) {
    course.showPassword = !course.showPassword; // Toggle password visibility flag
}
  getUserIdFromLocalStorage(): any | null {
    const userId = localStorage.getItem('userID');
    return userId ? +userId : null; // Convert to number or return null if not found
  }





}
