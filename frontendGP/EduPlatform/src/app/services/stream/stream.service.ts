import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Announcement } from 'src/app/models/announcement.model';
import { Comment } from 'src/app/models/comment.model';
import { data } from 'autoprefixer';
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
  getLabs(courseId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/announcment/${courseId}/labs`);
  }
  createAssignment(courseId: number, instructorId: number, dueDate: Date, title: string, content: string, allowLateSubmissions: boolean, file: File) {
    const formData = new FormData();
    formData.append('courseId', courseId.toString());
    formData.append('instructorId', instructorId.toString());
    formData.append('dueDate', dueDate.toISOString());
    formData.append('title', title);
    formData.append('content', content);
    formData.append('allowLateSubmissions', allowLateSubmissions.toString());
    formData.append('file', file);
    return this.http.post<void>(`${this.apiUrl}/api/assignments/${courseId}/create`, formData);
  }
  getAssignments(courseId:number){
    return this.http.get<any[]>(`${this.apiUrl}/announcment/${courseId}/assignments`);
  }
  getQuizzes(instructorId:string,courseId:number){
    return this.http.get<any[]>(`${this.apiUrl}/quizzes/get-course-quizzes/${instructorId}/${courseId}`)
  }
}
