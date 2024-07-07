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
  private instructorUrl = 'http://localhost:8080/submissions/instructor/quiz';

  constructor(private http: HttpClient) { }

  getQuizSubmissionsForStudent(studentId: number): Observable<QuizSubmissionDTO[]> {
    const url = `${this.apiUrl}/student/${studentId}`;
    return this.http.get<QuizSubmissionDTO[]>(url);
  }

  getQuizSubmissionsForInstructor(instructorId: string, quizId: number): Observable<any> {
    return this.http.get<any>(`${this.instructorUrl}/${instructorId}/${quizId}`);
  }
  downloadSubmissionPdf(studentId: number, quizId: number): Observable<Blob> {
    const apiUrl = 'http://localhost:8080/quizzes/generate-submission-pdf/'
    const url = `${apiUrl}${studentId}/${quizId}`;
    return this.http.get(url, { responseType: 'blob' });
  }

  
}
