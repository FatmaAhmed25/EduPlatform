import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-course-details',
  templateUrl: './course-details.component.html',
  styleUrls: ['./course-details.component.css']
})
export class CourseDetailsComponent implements OnInit {
  courseId: number = 1; // Example course ID
  studentId: number = 3; // Example student ID

  constructor() { }

  ngOnInit(): void {
  }
}
