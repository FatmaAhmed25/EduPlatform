import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/authService/auth.service'; // Ensure this path is correct
import { MatSnackBar } from '@angular/material/snack-bar';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  isPasswordVisible: boolean = false;

  constructor(private authService: AuthService, private router: Router, private snackBar:MatSnackBar) {}

  togglePasswordVisibility() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  onSubmit() {
    this.authService.login(this.email, this.password).subscribe(
      response => {
        this.authService.setToken(response.token);
        localStorage.setItem('userID', response.userID);
        
        localStorage.setItem('userType', response.userType);
        console.log(response.userType);
        if (response.userType === 'ROLE_STUDENT') {
          this.router.navigate(['/student-courses']);
        } else if (response.userType === 'ROLE_INSTRUCTOR') {
          this.router.navigate(['/instructor-courses']);
        }
        else if (response.userType === 'ROLE_ADMIN') {
          this.router.navigate(['/admin-dashboard']);
        }
      },
      error => {
        this.snackBar.open('Email/Password is incorrect Please check your Credentials', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
      });
        console.error('Login failed', error);
      }
    );
  }
}
