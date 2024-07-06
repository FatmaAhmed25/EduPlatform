import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UpcomingQuizDTO } from 'src/app/models/dtos/UpcomingQuizDTO';

@Injectable({
  providedIn: 'root'
})
export class UpcomingQuizzesService {

  private apiUrl = 'http://localhost:8080/quizzes/get-upcoming-quizzes/student/';

  constructor(private http: HttpClient) { }

  getUpcomingQuizzes(studentId: number): Observable<UpcomingQuizDTO[]> {
    return this.http.get<UpcomingQuizDTO[]>(`${this.apiUrl}${studentId}`);
  }
}