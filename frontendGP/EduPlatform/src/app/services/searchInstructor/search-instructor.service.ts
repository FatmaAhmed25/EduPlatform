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
export class SearchInstructorService {

  private baseUrl = 'http://localhost:8080/courses';
  private searchResultsSubject = new BehaviorSubject<Course[]>([]);
  searchResults$ = this.searchResultsSubject.asObservable();
  constructor(private http: HttpClient) { }

  getCoursesForInstructor(instructorId: String): Observable<any> {
    const url = `${this.baseUrl}/${instructorId}/get-courses/instructor`;
    return this.http.get(url);
  }
  setSearchResults(results: Course[]): void {
    this.searchResultsSubject.next(results);
  }
}
