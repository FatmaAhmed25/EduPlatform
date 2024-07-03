// In lab.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LabService {
  private baseUrl = 'http://localhost:8080/announcment';
  private coursesBaseUrl = 'http://localhost:8080/courses';

  constructor(private http: HttpClient) {}

  getLabs(courseId: number): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any>(`${this.baseUrl}/${courseId}/labs`, { headers });
  }

  getFileLink(courseId: number, fileName: string): Observable<string> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const encodedFileName = encodeURIComponent(fileName);
    return this.http.get(`${this.coursesBaseUrl}/${courseId}/get-content?fileName=${encodedFileName}`, { headers, responseType: 'text' }) as Observable<string>;
  }
}
