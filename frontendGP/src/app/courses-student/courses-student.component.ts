import { Component } from '@angular/core';

@Component({
  selector: 'app-courses-student',
  templateUrl: './courses-student.component.html',
  styleUrls: ['./courses-student.component.css']
})
export class CoursesStudentComponent {
    progress: number = 0; // Initial progress value

  myCourses = [
    { title: 'SMM Expert', nextLesson: 'Next Lesson: 08.07.2024', completion: 86, image: 'coursesPics/course1.webp' },
    { title: 'Web Design Basic', nextLesson: 'Next Lesson: 06.07.2024', completion: 50, image: 'coursesPics/course2.jpg' },
    { title: 'Motion Design', nextLesson: 'Course Passed', completion: 100, image: 'coursesPics/course3.webp' },
    { title: 'Photoshop', nextLesson: 'Course Passed', completion: 100, image: 'coursesPics/course4.jpg' },
    { title: 'Structured Programming', nextLesson: 'Course Passed', completion: 100, image: 'coursesPics/course5.avif' },

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
