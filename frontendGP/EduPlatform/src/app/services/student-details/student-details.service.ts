import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UserDTO } from 'src/app/models/dtos/usersdto';

@Injectable({
  providedIn: 'root'
})
export class StudentDetailsService {
  private baseUrl = 'http://localhost:8080/students';

  constructor(private http: HttpClient) { }

  updateStudentProfile(userDTO: any): Observable<HttpResponse<any>> {
    const { userId, currentPassword, newPassword, bio } = userDTO;
    let url = `${this.baseUrl}/update-profile?userId=${userId}`;

    if (currentPassword) {
      url += `&currentPassword=${currentPassword}`;
    }
    if (newPassword) {
      url += `&newPassword=${newPassword}`;
    }
    if (bio) {
      url += `&newBio=${bio}`;
    }

    return this.http.put<any>(url, {}, { observe: 'response' }) // Sending an empty body
      .pipe(catchError(this.handleError));
  }

  getStudentDetails(userId: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.baseUrl}/details/${userId}`)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Unknown error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    return throwError(errorMessage);
  }
}
