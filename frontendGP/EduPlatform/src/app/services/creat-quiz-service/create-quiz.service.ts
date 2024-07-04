import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { Quiz } from 'src/app/models/Quiz';

@Injectable({
  providedIn: 'root'
})
export class CreateQuizService {

  private apiUrl = 'http://localhost:8080/quizzes';
  token = localStorage.getItem('authToken');

  constructor(private http: HttpClient) {}

  private quiz: Quiz | null = null;
  setQuiz(quiz: Quiz) {
    this.quiz = quiz;
  }

  setQuizId(id: number) {
    if (this.quiz) {
      this.quiz.quizId = id;
    }
  }
  

  getQuiz(): Quiz | null {
    return this.quiz;
  }
  private createFormData(files: File[], quiz: Quiz): FormData {
    const formData = new FormData();

    // Append files to FormData
    files.forEach(file => {
        formData.append('pdfFiles', file, file.name);
    });
    if (quiz.instructorId !== undefined) {
      formData.append('instructorId', quiz.instructorId.toString());
  }

    // Append other quiz details with fallback for undefined values
    if (quiz.courseId !== undefined) {
        formData.append('courseId', quiz.courseId.toString());
    }

    if (quiz.assessmentName) {
        formData.append('quizTitle', quiz.assessmentName);
        console.log(quiz.assessmentName);
    }

    if (quiz.startDate) {
        formData.append('startTime', quiz.startDate.toString());
        console.log(quiz.startDate)
    }

    if (quiz.endDate) {
        formData.append('endTime', quiz.endDate.toString());
    }
    if (quiz.numOfQuestions) {
      formData.append('numOfQuestions', quiz.numOfQuestions.toString());
  }

    return formData;
  }

  submitQuiz(files: File[]): Observable<any> {
    const  url = this.apiUrl + '/generateMcq';
    if (this.quiz === null) {
      console.error('Quiz is not defined.');
      return of({ error: 'Quiz is not defined. Please set the quiz before submitting.' });
    }

    const formData = this.createFormData(files, this.quiz);
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    return this.http.post<any>(url, formData, { headers }).pipe(
      catchError(error => {
        console.error('Error submitting quiz:', error);
        return of({ error: 'Error submitting quiz. Please try again later.' });
      })
    );
  }
  getQuizForInstructor(): Observable<any> {
    console.log('Getting quiz')
    if (this.quiz === null) {
      console.error('Quiz is not defined.');
      return of({ error: 'Quiz is not defined. Please set the quiz before trying to access it.' });
    }
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);
    const instructorId = this.quiz.instructorId.toString();
    const courseId = this.quiz.courseId.toString();
    const quizId = this.quiz.quizId.toString();
    console.log(instructorId+ " ,"+courseId+ ","+quizId)
    return this.http.get<any>(`${this.apiUrl}/${instructorId}/${courseId}/${quizId}/instructor`, { headers }).pipe(
      catchError(error => {
        console.error('Error submitting quiz:', error);
        return of({ error: 'Error getting quiz. Please try again later.' });
      })
    );
  }
//   return this.http.get<any>(`${this.apiUrl}/7/1/2/instructor`, { headers }).pipe(
//     catchError(error => {
//       console.error('Error submitting quiz:', error);
//       return of({ error: 'Error submitting quiz. Please try again later.' });
//     })
//   );
// }
}
