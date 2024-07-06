import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UserDTO } from '../models/dtos/usersdto';
import { CourseDTO } from '../models/dtos/coursedto';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private baseUrl = 'http://localhost:8080/admin';  // Base URL for the API

  constructor(private http: HttpClient) { }


  importStudents(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.baseUrl}/import/students`, formData)
      .pipe(
        catchError(this.handleError)
      );
  }

  importInstructors(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.baseUrl}/import/instructors`, formData)
      .pipe(
        catchError(this.handleError)
      );
  }

  createStudent(userDTO: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/create/student`, userDTO)
      .pipe(
        catchError(this.handleError)
      );
  }

  createInstructor(userDTO: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/create/instructor`, userDTO)
      .pipe(
        catchError(this.handleError)
      );
  }

  resetPassword(userId: number, newPassword: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/reset-password?userId=${userId}&newPassword=${newPassword}`, null)
      .pipe(
        catchError(this.handleError)
      );
  }

  updateAdminProfile(userDTO: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/update-my-profile`, userDTO,{ observe: 'response' })
      .pipe(
        catchError(this.handleError)
      );
  }

  updateOtherUserProfile(userDTO: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/update-profile`, userDTO,{ responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }

  getNumberOfUsers(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/number-of-users`).pipe(
      catchError(this.handleError)
    );
  }

  getNumberOfStudents(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/number-of-students`).pipe(
      catchError(this.handleError)
    );
  }

  getNumberOfInstructors(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/number-of-instructors`).pipe(
      catchError(this.handleError)
    );
  }

  getNumberOfCourses(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/number-of-courses`).pipe(
      catchError(this.handleError)
    );
  }

  getAllStudents(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.baseUrl}/students`).pipe(
      catchError(this.handleError)
    );
  }

  getAllInstructors(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.baseUrl}/instructors`).pipe(
      catchError(this.handleError)
    );
  }

  getUserById(userId: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.baseUrl}/users/${userId}`).pipe(
      catchError(this.handleError)
    );
  }
  getAllCoursesWithDetails(): Observable<CourseDTO[]> {
    return this.http.get<CourseDTO[]>(`${this.baseUrl}/courses/details`).pipe(
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

  searchUsers(searchTerm: string): Observable<UserDTO[]> {
    console.log(searchTerm)
    return this.http.get<UserDTO[]>(`${this.baseUrl}/searchUsers?searchTerm=${searchTerm}`);
  }


}
