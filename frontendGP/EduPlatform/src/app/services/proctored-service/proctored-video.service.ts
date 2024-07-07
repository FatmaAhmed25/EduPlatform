import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProctoredVideoService {
  private baseUrl = 'http://localhost:8080/cheating-report';

  constructor(private http: HttpClient) {}

  savePhoto(studentId: string, quizId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('studentId', studentId.toString());
    formData.append('quizId', quizId.toString());
    formData.append('file', file);


    return this.http.post(`${this.baseUrl}/save-photo`, formData);
  }
}
