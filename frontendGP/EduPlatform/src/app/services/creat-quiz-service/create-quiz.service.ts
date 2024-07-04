import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CreateQuizService {

  private apiUrl = 'http://localhost:5000/chat';

  constructor(private http: HttpClient) {}

  // Method to submit quiz data
  submitQuiz(formData: FormData): Observable<any> {
    return this.http.post<any>(this.apiUrl, formData, {
      headers: new HttpHeaders({
        // 'Content-Type': 'multipart/form-data' should not be set manually because it will be set automatically
      })
    });
  }
}
