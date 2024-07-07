import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
// import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loginUrl = 'http://localhost:8080/auth/authenticate-users';

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(this.loginUrl, { email, password });
  }

  setToken(token: string) {
    localStorage.setItem('authToken', token);
  }

  getToken() {
    return localStorage.getItem('authToken');
  }

  setUserRole(role: string) {
    localStorage.setItem('userRole', role);
  }

  getUserRole() {
    return localStorage.getItem('userRole');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    this.clearCookies();
    window.location.reload(); // Reload the page to clear cache
  }

  private clearCookies() {
    const cookies = document.cookie.split(";");

    for (let i = 0; i < cookies.length; i++) {
      const cookie = cookies[i];
      const eqPos = cookie.indexOf("=");
      const name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
      document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/";
    }
  }
}
