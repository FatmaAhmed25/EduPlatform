import { Component } from '@angular/core';
import { CreateCourseService } from 'src/app/services/createCourse/create-course.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
@Component({
  selector: 'app-create-course',
  templateUrl: './create-course.component.html',
  styleUrls: ['./create-course.component.scss']
})
export class CreateCourseComponent {
  courseTitle: string = '';
  courseDescription: string = '';
  constructor(private courseService: CreateCourseService,private snackBar:MatSnackBar,private route:Router) { }

  createCourse() {
    console.log(this.courseTitle+" "+this.courseDescription)
    this.courseService.createCourse(this.courseTitle, this.courseDescription).subscribe(
      response => {
        this.snackBar.open('Course created successfully!', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
      },
      error => {
        this.snackBar.open('Error loading assignments', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
      }
    );
    this.route.navigate(['/instructor-courses']);
  }
}
