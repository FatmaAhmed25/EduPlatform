import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {
  private baseUrl = 'http://localhost:8080/announcment/get-announcement';

  constructor(private http: HttpClient) { }

  getAnnouncement(id: number): Observable<any> {
    const token = localStorage.getItem('authToken');  // Assuming the token is stored in local storage
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.get(`${this.baseUrl}/${id}`, { headers });
  }
}
