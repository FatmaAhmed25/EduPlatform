import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, catchError, tap, throwError } from 'rxjs';

export interface Course {
  courseId: number;
  courseCode: string;
  title: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private baseUrl = 'http://localhost:8080/courses/search';
  private searchResultsSubject = new BehaviorSubject<Course[]>([]);
  searchResults$ = this.searchResultsSubject.asObservable();

  constructor(private http: HttpClient) {}

  searchByCode(code: string): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.baseUrl}/by-code?courseCode=${code}`).pipe(
      tap(results => {
        this.searchResultsSubject.next(results);
      }),
      catchError(error => {
        console.error('Error searching courses by code:', error);
        return throwError(error);
      })
    );
  }

  searchByTitle(title: string): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.baseUrl}/by-title?title=${title}`).pipe(
      tap(results => {
        this.searchResultsSubject.next(results);
      }),
      catchError(error => {
        console.error('Error searching courses by title:', error);
        return throwError(error);
      })
    );
  }
}
