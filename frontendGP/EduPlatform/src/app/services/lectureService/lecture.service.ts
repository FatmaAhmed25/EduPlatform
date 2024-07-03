import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LectureService {
  private baseUrl = 'http://localhost:8080/announcment';
  private coursesBaseUrl = 'http://localhost:8080/courses';

  constructor(private http: HttpClient) {}

  getLectures(studentId: number, courseId: number): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization',  `Bearer ${token}`);
    console.log("hereeeeeeeee  eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlc3NtYXRAZXNzbWF0LmNvbSIsImlhdCI6MTcxOTk2MzI2MCwiZXhwIjoxNzE5OTcwNDYwfQ.Z7lz9swtjOE48tonPhJw6ZUgNzh1swfEzTn5sj876D0")
    return this.http.get<any>(`${this.baseUrl}/${studentId}/${courseId}/lectures/student`, { headers,  });
  }
  getFileLink(courseId: number, fileName: string): Observable<string> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5b3Vzc2VmLmVzc21hdC55ZUBnbWFpbC5jb20iLCJpYXQiOjE3MTk5Njg3OTUsImV4cCI6MTcxOTk3NTk5NX0.W9712wbl0yqKvHSyPPAlfhs2L5_eZMuncTE6D0o3zYk`);
    const encodedFileName = encodeURIComponent(fileName);
    return this.http.get(`${this.coursesBaseUrl}/${courseId}/get-content?fileName=${encodedFileName}`, { headers, responseType: 'text' }) as Observable<string>;
  }
}
