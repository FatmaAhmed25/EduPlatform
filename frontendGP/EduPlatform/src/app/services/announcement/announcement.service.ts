import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {
  private INstructorBaseUrl = 'http://localhost:8080/instructor';
  private CoursesBaseUrl = 'http://localhost:8080/courses';
  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  getAnnouncementsByCourseId(courseId: number): Observable<any> {
    const token = localStorage.getItem('authToken'); // Assuming the token is stored in local storage
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${this.INstructorBaseUrl}/${courseId}/announcements`, { headers });
  }
  getFileLink(courseId: number, fileName: string): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5b3Vzc2VmLmVzc21hdC55ZUBnbWFpbC5jb20iLCJpYXQiOjE3MTk4MTA0MzksImV4cCI6MTcxOTgxNzYzOX0.4yRDgEp9VUWcggEgWSOK01DQPxJwU9W1vLCRPkPAdkA`);
    const encodedFileName = encodeURIComponent(fileName); // Encode the file name
    return this.http.get(`${this.CoursesBaseUrl}/${courseId}/get-content?fileName=${fileName}`, { headers, responseType: 'text' });
  }
  getCommentsByAnnouncementId(announcementId: number): Observable<any> {
    const token = localStorage.getItem('authToken'); // Assuming the token is stored in local storage
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${this.baseUrl}/announcement/getComments/${announcementId}`);
  }
}
