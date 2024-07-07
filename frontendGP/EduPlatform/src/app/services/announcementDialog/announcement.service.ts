import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Instructor } from 'src/app/models/instructor.model';
import { Courses } from 'src/app/models/course.model';
@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {
  private apiUrl = "http://localhost:8080";

  constructor(private http: HttpClient) {}

  createAnnouncement(courseId: number, instructorId: number, title: string, content: string): Observable<any> {
    const announcementData = { title, content };
    return this.http.post(`${this.apiUrl}/announcment/${courseId}/${instructorId}/create-announcement`, announcementData);
  }

  uploadAnnouncementWithFile(courseId: number, instructorId: number, title: string, content: string, file: File, materialType: string): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    formData.append('content', content);
    formData.append('materialType', materialType);
    return this.http.post(`${this.apiUrl}/announcment/${instructorId}/courses/${courseId}/upload-material`, formData);
  }

  getFileLink(courseId: number, fileName: string): Observable<any> {
    const encodedFileName = encodeURIComponent(fileName); // Encode the file name
    return this.http.get(`${this.apiUrl}/courses/${courseId}/get-content?fileName=${encodedFileName}`, { responseType: 'text' });
  }

  getInstructorDetails(instructorId: string): Observable<Instructor> {
    return this.http.get<Instructor>(`${this.apiUrl}/instructor/${instructorId}`);
  }
  getCourseById(courseId: number): Observable<Courses> {
    return this.http.get<Courses>(`${this.apiUrl}/courses/instructor/courses/details/${courseId}`);
  }
  getCommentsByAnnouncementId(announcementId: number): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get(`${this.apiUrl}/announcment/getComments/${announcementId}`, { headers });
  }
  updateAnnouncement(courseId: number, instructorId: string, announcementId: number,
    title: string, content: string, materialType: string, file: File): Observable<any> {
      const formData = new FormData();
      formData.append('title', title);
      formData.append('content', content);
      formData.append('materialType', materialType);
      if (file) {
      formData.append('file', file, file.name);
      }
      const url = `${this.apiUrl}/announcment/${courseId}/${instructorId}/update-announcement/${announcementId}`;
      const params = new HttpParams()
      .set('courseId', courseId.toString())
      .set('instructorId', instructorId.toString())
      .set('announcementId', announcementId.toString());

      const headers = new HttpHeaders();
      headers.append('Content-Type', 'multipart/form-data');
      headers.append('Accept', 'application/json');

      return this.http.post<any>(url, formData, { params, headers });
}
  deleteAnnouncement(courseId:number,instructorId:string,announcementId:number){
    return this.http.delete(`${this.apiUrl}/announcment/${courseId}/${instructorId}/${announcementId}`);
  }
  isAssignment(announcementId:number){
    return this.http.get(`${this.apiUrl}/announcment/announcement/${announcementId}/is-assignment`);
  }
  
}