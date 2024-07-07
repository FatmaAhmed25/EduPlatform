import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AssignmentSubmissionsService {
  private apiUrl = "http://localhost:8080";
  constructor(private http: HttpClient) {}
  getSubmissions(assignmentId:number){
    return this.http.get<any[]>(`${this.apiUrl}/api/assignments/submissions/${assignmentId}`);
  }
  updateAssignment(
    assignmentId: number,
    courseId: number,
    instructorId: string,
    title: string,
    content: string,
    dueDate: Date,
    allowLateSubmission: boolean,
    file: File
  ): Observable<any> {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('content', content);
    formData.append('dueDate', dueDate.toISOString());
    formData.append('allowLateSubmission', allowLateSubmission.toString());
    if (file) {
      formData.append('file', file, file.name);
    }

    const url = `${this.apiUrl}/api/assignments/update-assignment/${assignmentId}`;
    const params = new HttpParams()
      .set('courseId', courseId.toString())
      .set('instructorId', instructorId);

    const headers = new HttpHeaders();
    headers.append('Accept', 'application/json');
    const headersWithContentType = new HttpHeaders({ 'Content-Type': 'multipart/form-data' });

    return this.http.post<any>(url, formData, { params, headers: headersWithContentType });
  }
}
