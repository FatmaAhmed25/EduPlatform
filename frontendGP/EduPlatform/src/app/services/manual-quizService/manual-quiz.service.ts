import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ManualQuizService {
  private baseUrl = 'http://localhost:8080/quizzes';

  constructor(private http: HttpClient) {}

  createQuiz(quizData: any): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(this.baseUrl, quizData, { headers });
  }
}
