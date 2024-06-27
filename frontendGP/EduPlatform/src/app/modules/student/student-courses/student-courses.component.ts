import { Component } from '@angular/core';

@Component({
  selector: 'app-student-courses',
  templateUrl: './student-courses.component.html',
  styleUrls: ['./student-courses.component.css']
})

export class StudentCoursesComponent {

    progress: number = 0; // Initial progress value

  myCourses = [
    { title: 'SMM Expert', image: 'courses/course1.webp' },
    { title: 'Web Design Basic', image: 'courses/course2.jpg' },
    { title: 'Motion Design', image: 'courses/course3.webp' },
    { title: 'Photoshop',  image: 'courses/course4.jpg' },
    { title: 'Photoshop',  image: 'courses/course4.jpg' },
    { title: 'Structured Programming', image: 'courses/course5.avif' },
  ];
  increaseProgress() {
    if (this.progress < 100) {
      this.progress += 10; // Increase progress by 10% (adjust as needed)
      // Adjust color based on progress
      this.updateProgressBarColor();
    }
  }

  updateProgressBarColor() {
    const progressBar = document.querySelector('.progress-bar') as HTMLElement;
    if (progressBar) {
      // Example logic to change color based on progress
      if (this.progress <= 30) {
        progressBar.style.backgroundColor = '#FF5733'; // color for early stages
      } else if (this.progress <= 60) {
        progressBar.style.backgroundColor = '#FFC300'; // color for mid stages
      } else {
        progressBar.style.backgroundColor = '#4CAF50'; // color for final stages
      }
    }
  }

}