import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UserDTO } from 'src/app/models/dtos/usersdto';
import { CourseDTO } from 'src/app/models/dtos/coursedto';

@Injectable({
  providedIn: 'root'
})
export class StudentDetailsService {

  private baseUrl = 'http://localhost:8080/students';  // Base URL for the API

  constructor(private http: HttpClient) { }







  updateStudentProfile(userDTO: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/update-profile`, userDTO,{ observe: 'response' })
      .pipe(
        catchError(this.handleError)
      );
  }


 




  getStudentDetails(userId: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.baseUrl}/details/${userId}`).pipe(
      catchError(this.handleError)
    );
  }


  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Unknown error occurred';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(errorMessage);
  }


}
