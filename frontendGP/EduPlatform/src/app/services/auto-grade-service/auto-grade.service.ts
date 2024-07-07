import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UngradedQuizDTO } from 'src/app/models/dtos/ungraded-quiz-dto';

@Injectable({
  providedIn: 'root'
})
export class AutoGradeService {

  private apiUrl = 'http://localhost:8080/quizzes/course';

  constructor(private http: HttpClient) { }

  getUngradedQuizzes(courseId: number): Observable<UngradedQuizDTO[]> {
    return this.http.get<UngradedQuizDTO[]>(`${this.apiUrl}/${courseId}/null-essay-submissions`);
  }
}
