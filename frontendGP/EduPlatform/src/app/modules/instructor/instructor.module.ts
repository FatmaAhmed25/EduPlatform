import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InstructorDashboardComponent } from './instructor-dashboard/instructor-dashboard.component';
import { CreateQuizComponent } from 'src/app/modules/instructor/create-quiz/create-quiz.component';
import { AiGenerateQuizComponent } from './ai-generate-quiz/ai-generate-quiz.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms'; 
import { CreateCourseComponent } from './create-course/create-course.component';
import { CoursesComponent } from './courses/courses.component';
import { SearchResultInstructorComponent } from './search-result-instructor/search-result-instructor.component';
import { StreamComponent } from './stream/stream.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { StudentDetailsDialogComponent } from './student-details-dialog/student-details-dialog.component';
import { MatListModule } from '@angular/material/list';
import { AnnouncementDialogComponent } from './announcement-dialog/announcement-dialog.component';
import { MatIconModule } from '@angular/material/icon';
import { TudentDetailsDialogComponent } from './tudent-details-dialog/tudent-details-dialog.component';
import { LabsComponent } from './labs/labs.component';
import { LecturesComponent } from './lectures/lectures.component';
@NgModule({
  declarations: [
    InstructorDashboardComponent,
    CreateQuizComponent,
    AiGenerateQuizComponent,
    CreateCourseComponent,
    CoursesComponent,
    SearchResultInstructorComponent,
    StreamComponent,
    StudentDetailsDialogComponent,
    AnnouncementDialogComponent,
    TudentDetailsDialogComponent,
    LabsComponent,
    LecturesComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatTabsModule,
    MatListModule,
    MatIconModule
  ],
  exports: [
    CreateQuizComponent,
  AiGenerateQuizComponent,
  InstructorDashboardComponent
]
})
export class InstructorModule { }
