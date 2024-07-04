import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InstructorDashboardComponent } from './instructor-dashboard/instructor-dashboard.component';
import { CreateQuizComponent } from 'src/app/modules/instructor/create-quiz/create-quiz.component';
import { AiGenerateQuizComponent } from './ai-generate-quiz/ai-generate-quiz.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms'; 

@NgModule({
  declarations: [
    InstructorDashboardComponent,
    CreateQuizComponent,
    AiGenerateQuizComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ReactiveFormsModule,
  ],
  exports: [
    CreateQuizComponent,
  AiGenerateQuizComponent,
  InstructorDashboardComponent
]
})
export class InstructorModule { }
