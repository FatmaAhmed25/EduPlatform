import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StudentSubmissionDTO } from 'src/app/models/dtos/student-submission-dto';
import { UngradedQuizDTO } from 'src/app/models/dtos/ungraded-quiz-dto';

@Injectable({
  providedIn: 'root'
})
export class AutoGradeService {

  private baseUrl = 'http://localhost:8080';
  public files: File[] = [];

  constructor(private http: HttpClient) { }

  getUngradedQuizzes(courseId: number): Observable<UngradedQuizDTO[]> {
    return this.http.get<UngradedQuizDTO[]>(`${this.baseUrl}/quizzes/course/${courseId}/null-essay-submissions`);
  }

  getStudentSubmissions(quizId: number): Observable<StudentSubmissionDTO[]> {
    const url = `${this.baseUrl}/api/quiz/getStudentSubmissions?quizId=${quizId}`;
    return this.http.get<StudentSubmissionDTO[]>(url);
  }

  setFiles(files: File[]): void {
    this.files = files;
  }


  autoGradeQuiz(quizId: number, studentId: number): Observable<any> {
    const url = `${this.baseUrl}/autograde`;

    // Create HttpParams and append query parameters
    let params = new HttpParams()
      .set('quizId', quizId.toString())
      .set('studentId', studentId.toString());

    const formData = new FormData();
    this.files.forEach(file => formData.append('pdfFiles', file));

    const headers = new HttpHeaders({
      'enctype': 'multipart/form-data'
    });

    return this.http.post(url, formData, { headers, params });
  }
}
