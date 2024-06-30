import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-course-details',
  templateUrl: './course-details.component.html',
  styleUrls: ['./course-details.component.css']
})
export class CourseDetailsComponent implements OnInit {
  announcements = [
    { title: 'Announcement 1', content: 'Content for announcement 1', date: new Date() },
    { title: 'Announcement 2', content: 'Content for announcement 2', date: new Date() }
  ];

  lectures = [
    { title: 'Lecture 1', description: 'Description for lecture 1' },
    { title: 'Lecture 2', description: 'Description for lecture 2' }
  ];

  labs = [
    { title: 'Lab 1', description: 'Description for lab 1' },
    { title: 'Lab 2', description: 'Description for lab 2' }
  ];

  assignments = [
    { title: 'Assignment 1', description: 'Description for assignment 1' },
    { title: 'Assignment 2', description: 'Description for assignment 2' }
  ];

  constructor() { }

  ngOnInit(): void {
  }
}
