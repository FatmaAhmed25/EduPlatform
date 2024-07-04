import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class CreateCourseService {
  private baseUrl = 'http://localhost:8080/courses/create/course';

  constructor(private http: HttpClient) { }

  createCourse(title: string, description: string): Observable<any> {
    const instructorId = localStorage.getItem('userID');
    if (!instructorId) {
      throw new Error('Instructor ID not found in localStorage');
    }
    const url = `${this.baseUrl}/${instructorId}`;
    return this.http.post<any>(url, { title, description });
  }
}
