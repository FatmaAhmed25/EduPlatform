import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Quiz } from 'src/app/models/Quiz';

@Injectable({
  providedIn: 'root'
})
export class StudentQuizService {
 
  constructor(private http: HttpClient) { }

  private baseUrl = 'http://localhost:8080';
  token = localStorage.getItem('authToken');
  studentId = localStorage.getItem('userID');

  getQuiz(): Observable<Quiz> {
    const url = `${this.baseUrl}/quizzes/${this.studentId}/1/2/student`;
    return this.http.get<Quiz>(url).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404 && error.error === "Student has already submitted this quiz") {
          // Handle specific error message here
          return throwError("Student has already submitted this quiz");
        } else {
          // Handle other errors
          return throwError("Something went wrong. Please try again later.");
        }
      })
    );
  }
  submitQuiz(payload: { studentId: any; quizId: any; answers: { questionId: any; answerId: any; }[]; }) { 
    console.log(payload);
    const url = `${this.baseUrl}/api/quiz/submit/mcq-quiz`;
    return this.http.post<any>(url, payload);
  }

}
