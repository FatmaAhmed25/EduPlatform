import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InstructorDashboardComponent } from './instructor-dashboard/instructor-dashboard.component';
import { CreateQuizComponent } from './create-quiz/create-quiz.component';
import { AiGenerateQuizComponent } from './ai-generate-quiz/ai-generate-quiz.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { CreateCourseComponent } from './create-course/create-course.component';
import { CoursesComponent } from './courses/courses.component';


@NgModule({
  declarations: [
    InstructorDashboardComponent,
    CreateQuizComponent,
    AiGenerateQuizComponent,
    CreateCourseComponent,
    CoursesComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
  ],
  exports: [
    CreateQuizComponent,
  AiGenerateQuizComponent,
  InstructorDashboardComponent
]
})
export class InstructorModule { }
