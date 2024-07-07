import { Component, OnInit } from '@angular/core';
import { SidebarService } from 'src/app/services/sidebar-Service/sidebar.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EnrollmentService } from 'src/app/services/enrollment/enrollment.service';
import { Courses } from 'src/app/models/course.model';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from 'src/app/services/authService/auth.service'; // Import AuthService

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  constructor(private sidebarService: SidebarService, private snackBar: MatSnackBar, private enrollmentService: EnrollmentService, private router: Router, private authService: AuthService) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.activeLink = event.urlAfterRedirects;
      }
    });
  }
  selectedCourse: Courses | null = null;
  showModal: boolean = false;
  enrollMethod: string = '';
  enrollCode: string = '';
  enrollPassword: string = '';
  activeLink: string | null = null;
  userType: string | null = null;

  ngOnInit(): void {
    this.userType = localStorage.getItem('userType');
    console.log(this.userType)
    this.userType = localStorage.getItem('userType');
    console.log(this.userType)
    const body = document.body;
    const sidebar = document.querySelector('nav') as HTMLElement;
    const toggle = document.querySelector(".toggle") as HTMLElement;
    const searchBtn = document.querySelector(".search-box") as HTMLElement;
    const modeSwitch = document.querySelector(".toggle-switch") as HTMLElement;
    const modeText = document.querySelector(".mode-text") as HTMLElement;

    if (toggle) {
      toggle.addEventListener("click", () => {
        sidebar.classList.toggle("close");
        body.classList.toggle("sidebar-open");
      });
    }

    if (searchBtn) {
      searchBtn.addEventListener("click", () => {
        sidebar.classList.remove("close");
        body.classList.add("sidebar-open");
      });
    }

    if (modeSwitch) {
      modeSwitch.addEventListener("click", () => {
        body.classList.toggle("dark");
        // Update mode text
        if (body.classList.contains("dark")) {
          modeText.innerText = "Light mode";
        } else {
          modeText.innerText = "Dark mode";
        }
      });
    }
  }
  toggleSidebar() {
    this.sidebarService.toggleSidebar();
  }

  openEnrollModal(): void {
    this.activeLink = '/enroll-by-code';
    this.showModal = true;
  }
  isActive(link: string): boolean {
    return this.activeLink === link;
  }
  closeEnrollModal(): void {
    this.showModal = false;
    this.enrollMethod = '';
    this.enrollCode = '';
    this.enrollPassword = '';
    this.activeLink = '/student-courses';
  }
  enrollCourse(): void {
    const studentId = localStorage.getItem('userID');
    console.log(this.enrollCode + " " + this.enrollPassword)
    if (this.enrollCode && studentId && this.enrollPassword) {
      this.enrollmentService.enrollByCode(this.enrollCode, Number(studentId), this.enrollPassword).subscribe(
        (response) => {
          if (response.status === 200) {
            this.snackBar.open('Enrolled successfully by code', 'Close', {
              duration: 5000,
              verticalPosition: 'top',
              horizontalPosition: 'right'
            });
            this.closeEnrollModal();
          }
        },
        error => {
          if (error.status === 400) {
            this.snackBar.open('Course password or Code is incorrect', 'Close', {
              duration: 5000,
              verticalPosition: 'top',
              horizontalPosition: 'right'
            });
          }
          if (error.status === 401) {
            this.snackBar.open('You are already enrolled in the course', 'Close', {
              duration: 5000,
              verticalPosition: 'top',
              horizontalPosition: 'right'
            });
          }
        }
      );
    }

    else {
      this.snackBar.open('Missing required fields: selectedCourse, studentId, or enrollPassword', 'Close', {
        duration: 5000,
        verticalPosition: 'top',
        horizontalPosition: 'right'
      });
    }
  }
  // Method to handle logout
  logout(): void {
    this.authService.logout();
  }
}