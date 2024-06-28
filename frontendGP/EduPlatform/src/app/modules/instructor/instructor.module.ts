import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InstructorDashboardComponent } from './instructor-dashboard/instructor-dashboard.component';
import { CreateQuizComponent } from './create-quiz/create-quiz.component';
import { AiGenerateQuizComponent } from './ai-generate-quiz/ai-generate-quiz.component';



@NgModule({
  declarations: [
    InstructorDashboardComponent,
    CreateQuizComponent,
    AiGenerateQuizComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
  ],
  exports: [
    CreateQuizComponent,
  AiGenerateQuizComponent,
  InstructorDashboardComponent
]
})
export class InstructorModule { }
