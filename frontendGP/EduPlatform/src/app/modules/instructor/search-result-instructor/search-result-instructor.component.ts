import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SidebarService } from 'src/app/services/sidebar-Service/sidebar.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SearchInstructorService } from 'src/app/services/searchInstructor/search-instructor.service';
import { Course } from 'src/app/services/search/search.service';

interface Courses extends Course {
  password: string;
  assignments: any[];
  image: string;
}
@Component({
  selector: 'app-search-result-instructor',
  templateUrl: './search-result-instructor.component.html',
  styleUrls: ['./search-result-instructor.component.scss']
})
export class SearchResultInstructorComponent {

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
    searchResults: Courses[] = [];

    constructor(
      private instructorCoursesService: SearchInstructorService,
      private router: Router,
      private sidebarService: SidebarService,
      private snackBar: MatSnackBar
    ) {}
  
    ngOnInit(): void {
      this.instructorCoursesService.searchResults$.subscribe(results => {
        this.searchResults = results.map(course => ({
          ...course,
          password: '', // Assuming you modify this as necessary
          assignments: [], // Assuming you modify this as necessary
          image: this.getRandomImage() // Assuming you have getRandomImage() method
        }));
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
  
    deleteCourse(courseId: number): void {
      alert("Course deleted");
    }
  
    getRandomImage(): string {
      const randomIndex = Math.floor(Math.random() * this.images.length);
      return this.images[randomIndex];
    }
  }
  
