  import { Injectable } from '@angular/core';
  import { HttpClient, HttpHeaders } from '@angular/common/http';
  import { Observable } from 'rxjs';

  @Injectable({
    providedIn: 'root'
  })
  export class AssignmentService {
    private baseUrl = 'http://localhost:8080/announcment';
    private coursesBaseUrl = 'http://localhost:8080/courses';
    private assignURL = 'http://localhost:8080/api/assignments';

    constructor(private http: HttpClient) {}

    getAssignments(courseId: number): Observable<any> {
      const token = localStorage.getItem('authToken');
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.get<any>(`${this.baseUrl}/${courseId}/assignments`, { headers });
    }

    getFileLink(courseId: number, fileName: string): Observable<string> {
      const token = localStorage.getItem('authToken');
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      const encodedFileName = encodeURIComponent(fileName);
      return this.http.get(`${this.coursesBaseUrl}/${courseId}/get-content?fileName=${encodedFileName}`, { headers, responseType: 'text' }) as Observable<string>;
    }
    submitAssignment(studentId: string, assignmentId: number, file: File): Observable<any> {
      const token = localStorage.getItem('authToken');
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      const formData: FormData = new FormData();
      formData.append('file', file);
    
      // Specify that the response type is text
      return this.http.post(`${this.assignURL}/submit?studentId=${studentId}&assignmentId=${assignmentId}`, formData, { headers, responseType: 'text' });
    }
    
  }
