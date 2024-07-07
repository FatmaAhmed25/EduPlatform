import { Component, OnInit } from '@angular/core';
import { Course } from 'src/app/services/search/search.service';
import { SearchService } from 'src/app/services/search/search.service';
import { EnrollmentService } from 'src/app/services/enrollment/enrollment.service';
import { HttpResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
interface Courses extends Course {
  password: string;
  assignments: any[];
  image: string;
}

@Component({
  selector: 'app-search-result',
  templateUrl: './search-result.component.html',
  styleUrls: ['./search-result.component.scss']
})

export class SearchResultComponent implements OnInit {
  
  searchResults: Courses[] = [];
  images: string[] = [
    'assets/images/courses/course1.webp',
    'assets/images/courses/course2.jpg',
    'assets/images/courses/course3.webp',
    'assets/images/courses/course4.jpg',
    'assets/images/courses/course5.avif',
    'assets/images/courses/course6.jpg',
    'assets/images/courses/course7.png'
  ];
  showModal: boolean = false;
  enrollMethod: string = '';
  enrollCode: string = '';
  enrollPassword: string = '';
  selectedCourse: Courses | null = null;
  constructor(private searchService: SearchService,private enrollmentService:EnrollmentService,private snackBar: MatSnackBar,private route:Router) { }

  ngOnInit(): void {
    this.searchService.searchResults$.subscribe(results => {
      this.searchResults = results.map(course => ({
        ...course,
        password: '',
        assignments: [],
        image: this.getRandomImage()
      }));
    });
  }
  enroll(course: Courses): void {
    console.log(`Enrolling in course ${course.courseId}`);
    this.openEnrollModal(course);
  }

  openEnrollModal(course: Courses): void {
    this.selectedCourse = course;
    this.showModal = true;
  }

  closeEnrollModal(): void {
    this.showModal = false;
    this.enrollMethod = '';
    this.enrollCode = '';
    this.enrollPassword = '';
  }

  getRandomImage(): string {
    const randomIndex = Math.floor(Math.random() * this.images.length);
    return this.images[randomIndex];
  }
  enrollCourse(): void {
    const studentId = localStorage.getItem('userID');
    console.log(this.enrollPassword + " salamo3aleko")
    if (this.selectedCourse && studentId && this.enrollPassword) {
            this.enrollmentService.enrollByCourseId(this.selectedCourse.courseId, Number(studentId), this.enrollPassword).subscribe(
                (response: HttpResponse<void>) => {
                    if (response.status === 200) {
                        this.snackBar.open('Enrolled successfully by course ID', 'Close', {
                            duration: 5000,
                            verticalPosition: 'top',
                            horizontalPosition: 'right'
                        });
                        this.closeEnrollModal();
                        this.route.navigate(['/student-courses'])
                    }

                },
                error => {
                    if (error.status === 401) {
                        this.snackBar.open('Course password is incorrect', 'Close', {
                            duration: 5000,
                            verticalPosition: 'top',
                            horizontalPosition: 'right'
                        });
                    }
                    if (error.status === 400) {
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
}