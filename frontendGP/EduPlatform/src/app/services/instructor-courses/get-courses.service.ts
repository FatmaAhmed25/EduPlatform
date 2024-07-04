import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GetCoursesService {

  private apiUrl = 'http://localhost:8080'; // Replace with your backend URL

  constructor(private http: HttpClient) {}

  getInstructorCourses(instructorId: number): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any>(`${this.apiUrl}/courses/${instructorId}/get-courses/instructor`, { headers });
  }
  updateCourse(courseId: number, courseData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/instructor/courses/update-course-details/${courseId}`, courseData);
  }
  getCourseDetails(courseId: number): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any>(`${this.apiUrl}/courses/instructor/courses/details/${courseId}`, { headers });
  }
}
