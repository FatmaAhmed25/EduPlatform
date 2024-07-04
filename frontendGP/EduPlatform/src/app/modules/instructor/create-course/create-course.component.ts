import { Component } from '@angular/core';
import { CreateCourseService } from 'src/app/services/createCourse/create-course.service';
@Component({
  selector: 'app-create-course',
  templateUrl: './create-course.component.html',
  styleUrls: ['./create-course.component.scss']
})
export class CreateCourseComponent {
  courseTitle: string = '';
  courseDescription: string = '';
  constructor(private courseService: CreateCourseService) { }

  createCourse() {
    console.log(this.courseTitle+" "+this.courseDescription)
    this.courseService.createCourse(this.courseTitle, this.courseDescription).subscribe(
      response => {
        console.log('Course created successfully', response.message);
      },
      error => {
        console.error('Error creating course', error);
      }
    );
  }
}
