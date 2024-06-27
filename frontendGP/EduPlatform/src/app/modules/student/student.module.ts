import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudentDashboardComponent } from './student-dashboard/student-dashboard.component';
import { QuizComponent } from './quiz/quiz.component';
import { StudentCoursesComponent } from './student-courses/student-courses.component';



@NgModule({
  declarations: [
    StudentDashboardComponent,
    QuizComponent,
    StudentCoursesComponent
  ],
  imports: [
    CommonModule
  ],
  exports: [StudentDashboardComponent,
    StudentCoursesComponent]
})
export class StudentModule { }
