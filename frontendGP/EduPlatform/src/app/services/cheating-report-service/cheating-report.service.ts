import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CheatingReportService {

  private apiUrl = 'http://localhost:8080/cheating-report/cheating-report/';

  constructor(private http: HttpClient) { }

  downloadReport(submissionId: number): Observable<Blob> {
    const url = `${this.apiUrl}${submissionId}`;
    return this.http.get(url, { responseType: 'blob' });
  }
}