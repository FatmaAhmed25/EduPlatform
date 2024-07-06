import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/authService/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  isPasswordVisible: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  togglePasswordVisibility() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  onSubmit() {
    this.authService.login(this.email, this.password).subscribe(
      response => {
        // Save the token in local storage
        localStorage.setItem('authToken', response.token);
        localStorage.setItem('userID', response.userID);
        localStorage.setItem('userType',response.userType)
        
        // Check the user role and navigate accordingly
        if (response.userType === 'ROLE_STUDENT') {
          this.router.navigate(['/student-courses']);
        } else if (response.userType === 'ROLE_INSTRUCTOR') {
          this.router.navigate(['/instructor-dashboard']);
        }
        else if (response.userType === 'ROLE_ADMIN') {
          this.router.navigate(['/admin']);
        }
      },
      error => {
        console.error('Login failed', error);
      }
    );
  }
}
