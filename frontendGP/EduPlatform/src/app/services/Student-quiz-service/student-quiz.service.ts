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

  getQuiz(courseId:any,quizId:any): Observable<Quiz> {
    const url = `${this.baseUrl}/quizzes/${this.studentId}/${courseId}/${quizId}/student`;
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
  submitMCQQuiz(payload: { studentId: any; quizId: any; answers: { questionId: any; answerId: any; }[]; }) { 
    console.log(payload);
    const url = `${this.baseUrl}/api/quiz/submit/mcq-quiz`;
    return this.http.post<any>(url, payload);
  }
  submitEssayQuiz(payload: { studentId: number; quizId: any; answers: { questionId: any; answer: any; }[]; }) { 
    console.log(payload);
    const url = `${this.baseUrl}/api/quiz/submit/essay-quiz`;
    return this.http.post<any>(url, payload);
  }

}
