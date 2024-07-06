import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authToken = localStorage.getItem('authToken');
    const loginUrl = 'http://localhost:8080/auth/authenticate-users';

    if (authToken && req.url !== loginUrl) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${authToken}`)
      });

      return next.handle(cloned).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 401) {
            // Token has expired or is invalid, logout the user and redirect to login
            this.handleAuthError();
          }
          return throwError(error);
        })
      );
    } else {
      return next.handle(req);
    }
  }

  private handleAuthError() {
    localStorage.removeItem('authToken'); // Remove the token
    this.router.navigate(['/']); // Redirect to login page
  }
}
