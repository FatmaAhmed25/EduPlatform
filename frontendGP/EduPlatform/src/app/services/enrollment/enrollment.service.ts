import { Injectable, } from '@angular/core';
import { HttpClient ,HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class EnrollmentService {

  private baseUrl = 'http://localhost:8080/courses';

  constructor(private http: HttpClient) { }

  enrollByCode(courseCode: string, studentId: number, password: string): Observable<HttpResponse<void>> {
    const url = `${this.baseUrl}/enroll-by-code?courseCode=${courseCode}&studentId=${studentId}&password=${password}`;
    return this.http.post<void>(url, {}, { observe: 'response' }); 
  }

  enrollByCourseId(courseId: number, studentId: number, password: string): Observable<HttpResponse<void>> {
    const url = `${this.baseUrl}/${courseId}/enroll-student?studentId=${studentId}&password=${password}`;
    return this.http.post<void>(url, {}, { observe: 'response' }); 
  }
}

