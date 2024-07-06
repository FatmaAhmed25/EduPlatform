import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AssignmentSubmissionsService {
  private apiUrl = "http://localhost:8080";
  constructor(private http: HttpClient) {}
  getSubmissions(assignmentId:number){
    return this.http.get(`${this.apiUrl}/api/assignment/submissions/${assignmentId}`);
  }
}
