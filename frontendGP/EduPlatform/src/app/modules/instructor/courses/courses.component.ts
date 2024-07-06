import { Component, OnInit } from '@angular/core';
import { GetCoursesService } from 'src/app/services/instructor-courses/get-courses.service';
import { Router } from '@angular/router';
import { SidebarService } from 'src/app/services/sidebar-Service/sidebar.service';
import { MatSnackBar } from '@angular/material/snack-bar';
@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.scss']
})
export class CoursesComponent implements OnInit {
  isSidebarOpen = false;
  myCourses: any[] = []; 
  images: string[] = [
    'assets/images/courses/course1.webp',
    'assets/images/courses/course2.jpg',
    'assets/images/courses/course3.webp',
    'assets/images/courses/course4.jpg',
    'assets/images/courses/course5.avif',
    'assets/images/courses/course6.jpg',
    'assets/images/courses/course7.png'
  ];
  isModalOpen = false;
  selectedCourse: any = {};

  constructor(
    private instructorCoursesService: GetCoursesService,
    private router: Router,
    private sidebarService: SidebarService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const instructorId = localStorage.getItem('userID'); // Ensure the instructorId is stored in local storage
    if (instructorId) {
      this.instructorCoursesService.getInstructorCourses(Number(instructorId)).subscribe(
        (courses) => {
          this.myCourses = courses.map((course: any) => ({
            ...course,
            image: this.getRandomImage()
          }));
        },
        (error) => {
          console.error('Failed to load instructor courses', error);
          this.snackBar.open('Failed to load instructor courses', 'Close', {
            duration: 5000,
            verticalPosition: 'top',
            horizontalPosition: 'right'
        });
        }
      );
    } else {
      console.error('Instructor ID not found in local storage');
    }
    this.sidebarService.isOpen$.subscribe(isOpen => {
      this.isSidebarOpen = isOpen;
    });
  }

  goToCourse(courseId: number): void {
    this.router.navigate(['/stream', courseId]);
  }

  toggleDropdown(course: any): void {
    this.myCourses.forEach(c => {
      if (c !== course) {
        c.showDropdown = false;
      }
    });
    course.showDropdown = !course.showDropdown;
  }

  openUpdateModal(courseId: number): void {
    this.instructorCoursesService.getCourseDetails(courseId).subscribe(
      (course) => {
        this.selectedCourse = { ...course, courseId }; 
        this.isModalOpen = true;
        this.toggleDropdown(course);
       
      },
      (error) => {
        console.error('Failed to load course details', error);
        this.snackBar.open('Failed to load course details. Please try again later.', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
      });
      }
    );
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  updateCourse(): void {
    this.instructorCoursesService.updateCourse(this.selectedCourse.courseId, this.selectedCourse).subscribe(
      (response) => {
        console.log('Course updated successfully', response);
        this.closeModal();
        const index = this.myCourses.findIndex(course => course.courseId === this.selectedCourse.courseId);
        if (index !== -1) {
          this.myCourses[index] = { ...this.selectedCourse, image: this.myCourses[index].image };
        }
        this.snackBar.open('Course Details updated successfully!.', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
      });
      },
      (error) => {
        console.error('Failed to update course', error);
        this.snackBar.open('Failed to update course.', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
      });
      }
    );
  }

  deleteCourse(courseId: number): void {
    alert("Course deleted");
  }

  getRandomImage(): string {
    const randomIndex = Math.floor(Math.random() * this.images.length);
    return this.images[randomIndex];
  }
}
