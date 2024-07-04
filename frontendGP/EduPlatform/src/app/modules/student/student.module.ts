import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudentDashboardComponent } from './student-dashboard/student-dashboard.component';
import { QuizComponent } from './quiz/quiz.component';
import { StudentCoursesComponent } from './student-courses/student-courses.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NavbarComponent } from 'src/app/components/navbar/navbar.component';
import { AssignmentComponent } from './assignment/assignment.component';
// import { SubmitDialogComponent } from './submit-dialog/submit-dialog.component';
// import { LabComponent } from './lab/lab.component';
@NgModule({
  declarations: [
    StudentDashboardComponent,
    QuizComponent,
    StudentCoursesComponent,
    NavbarComponent,
    // SubmitDialogComponent,
    // AssignmentComponent,
    // LabComponent,
  ],
  imports: [
    CommonModule,
    MatProgressSpinnerModule,
  ],
  exports: [StudentDashboardComponent,
    StudentCoursesComponent]
})
export class StudentModule { }
