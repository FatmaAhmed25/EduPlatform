import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {
  private instructorBaseUrl = 'http://localhost:8080/instructor';
  private coursesBaseUrl = 'http://localhost:8080/courses';
  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }
  getAnnouncementsByCourseId(courseId: number): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${this.instructorBaseUrl}/${courseId}/announcements`, { headers });
  }

  getFileLink(courseId: number, fileName: string): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const encodedFileName = encodeURIComponent(fileName); // Encode the file name
    return this.http.get(`${this.coursesBaseUrl}/${courseId}/get-content?fileName=${encodedFileName}`, { headers, responseType: 'text' });
  }

  getCommentsByAnnouncementId(announcementId: number): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${this.baseUrl}/announcement/getComments/${announcementId}`, { headers });
  }
  getUserDetails(userId: string): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${this.baseUrl}/students/details/${userId}`, { headers });
  }
}
