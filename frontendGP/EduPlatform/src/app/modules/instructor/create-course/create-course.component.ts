import { Component } from '@angular/core';
import { CreateCourseService } from 'src/app/services/createCourse/create-course.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import {
  trigger,
  transition,
  style,
  animate,
  query,
  stagger,
} from '@angular/animations';

@Component({
  selector: 'app-create-course',
  templateUrl: './create-course.component.html',
  styleUrls: ['./create-course.component.scss'],
  animations: [
    trigger('formAnimations', [
      transition(':enter', [
        query('.form-group, h3, .btn-submit', [
          style({ opacity: 0, transform: 'translateY(20px) scale(0.95)' }),
          stagger(100, [
            animate(
              '600ms cubic-bezier(0.35, 0, 0.25, 1)',
              style({ opacity: 1, transform: 'translateY(0) scale(1)' })
            ),
          ]),
        ]),
      ]),
    ]),
  ],
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
