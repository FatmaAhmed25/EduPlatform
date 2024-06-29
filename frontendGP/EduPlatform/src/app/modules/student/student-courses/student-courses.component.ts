import { Component, OnInit } from '@angular/core';
import { Course } from 'src/app/models/course.model';
import { EnrolledCoursesService } from 'src/app/services/student-course/enrolled-courses.service';

@Component({
  selector: 'app-student-courses',
  templateUrl: './student-courses.component.html',
  styleUrls: ['./student-courses.component.css']
})
export class StudentCoursesComponent implements OnInit {
  progress: number = 0;
  myCourses: Course[] = []; // Use the Course interface
  images: string[] = [
    'assets/images/courses/course1.webp',
    'assets/images/courses/course2.jpg',
    'assets/images/courses/course3.webp',
    'assets/images/courses/course4.jpg',
    'assets/images/courses/course5.avif',
    'assets/images/courses/course6.jpg',
    'assets/images/courses/course7.png'
  ];
  constructor(private enrolledCoursesService: EnrolledCoursesService) {}

  ngOnInit(): void {
    const studentId = localStorage.getItem('userID'); // Ensure the studentId is stored in local storage
    if (studentId) {
      this.enrolledCoursesService.getEnrolledCourses(Number(studentId)).subscribe(
        (courses) => {
          this.myCourses = courses.map((course: any) => ({
            ...course,
            image: this.getRandomImage()
          }));        },
        (error) => {
          console.error('Failed to load enrolled courses', error);
        }
      );
    } else {
      console.error('Student ID not found in local storage');
    }
  }
  getRandomImage(): string {
    const randomIndex = Math.floor(Math.random() * this.images.length);
    return this.images[randomIndex];
  }
  increaseProgress() {
    if (this.progress < 100) {
      this.progress += 10;
      this.updateProgressBarColor();
    }
  }

  updateProgressBarColor() {
    const progressBar = document.querySelector('.progress-bar') as HTMLElement;
    if (progressBar) {
      if (this.progress <= 30) {
        progressBar.style.backgroundColor = '#FF5733';
      } else if (this.progress <= 60) {
        progressBar.style.backgroundColor = '#FFC300';
      } else {
        progressBar.style.backgroundColor = '#4CAF50';
      }
    }
  }
}
