import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Announcement } from 'src/app/models/announcement.model';
import { Comment } from 'src/app/models/comment.model';
@Injectable({
  providedIn: 'root'
})
export class StreamService {

  private apiUrl = 'http://localhost:8080'; // Replace with your API URL

  constructor(private http: HttpClient) {}

  getStudentsByCourseId(courseId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/students/by-course/${courseId}`);
  }
  getAnnouncementsByCourseId(courseId: number): Observable<Announcement[]> {
    return this.http.get<Announcement[]>(`${this.apiUrl}/instructor/${courseId}/announcements`);
  }

  getCommentsByAnnouncementId(announcementId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/announcement/getComments/${announcementId}`);
  }

  getLectures(courseId: number,instructorId:string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/announcment/${instructorId}/${courseId}/lectures/instructor`);
  
  }
  getVideos(courseId: number,instructorId:string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/announcment/${instructorId}/${courseId}/videos/instructor`);
  }
}
