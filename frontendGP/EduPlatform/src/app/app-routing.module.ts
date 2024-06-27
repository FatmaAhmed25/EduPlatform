import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AiGenerateQuizComponent } from './modules/instructor/ai-generate-quiz/ai-generate-quiz.component';
import { CreateQuizComponent } from './modules/instructor/create-quiz/create-quiz.component';
import { StudentCoursesComponent } from './modules/student/student-courses/student-courses.component';
const routes: Routes = [
  { path: '', component: CreateQuizComponent},
  { path: 'ai-generate-quiz', component: AiGenerateQuizComponent },
  { path: 'create-quiz', component: CreateQuizComponent},
  { path: 'student-courses', component: StudentCoursesComponent },

  

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
