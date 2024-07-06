// quiz-submissions.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { QuizSubmissionDTO } from 'src/app/models/dtos/quiz-submission-dto';


@Injectable({
  providedIn: 'root'
})
export class QuizSubmissionsService {

  private apiUrl = 'http://localhost:8080/submissions/quiz-submissions';

  constructor(private http: HttpClient) { }

  getQuizSubmissionsForStudent(studentId: number): Observable<QuizSubmissionDTO[]> {
    const url = `${this.apiUrl}/student/${studentId}`;
    return this.http.get<QuizSubmissionDTO[]>(url);
  }
}
