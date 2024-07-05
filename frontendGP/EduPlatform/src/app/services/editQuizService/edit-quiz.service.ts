import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Quiz } from 'src/app/models/Quiz';

@Injectable({
  providedIn: 'root'
})
export class EditQuizService {

  constructor(private http: HttpClient) { }

  private baseUrl = 'http://localhost:8080/quizzes';
  token = localStorage.getItem('authToken');

  updateQuiz(quizId: any, instructorId: any, quiz: Quiz): Observable<Quiz> {
    const url = `${this.baseUrl}/update-quiz/${quizId}/${instructorId}`;
    return this.http.put<Quiz>(url, quiz);
  }
}
