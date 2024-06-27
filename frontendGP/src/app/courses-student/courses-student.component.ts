import { Component } from '@angular/core';

@Component({
  selector: 'app-courses-student',
  templateUrl: './courses-student.component.html',
  styleUrls: ['./courses-student.component.css']
})
export class CoursesStudentComponent {
    progress: number = 0; // Initial progress value

  myCourses = [
    { title: 'SMM Expert', image: 'coursesPics/course1.webp' },
    { title: 'Web Design Basic', image: 'coursesPics/course2.jpg' },
    { title: 'Motion Design', image: 'coursesPics/course3.webp' },
    { title: 'Photoshop',  image: 'coursesPics/course4.jpg' },
    { title: 'Structured Programming', image: 'coursesPics/course5.avif' },

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
        progressBar.style.backgroundColor = '#FF5733'; // Example color for early stages
      } else if (this.progress <= 60) {
        progressBar.style.backgroundColor = '#FFC300'; // Example color for mid stages
      } else {
        progressBar.style.backgroundColor = '#4CAF50'; // Example color for final stages
      }
    }
  }

}
