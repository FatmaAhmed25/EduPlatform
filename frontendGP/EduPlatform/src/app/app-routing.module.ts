import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AiGenerateQuizComponent } from './modules/instructor/ai-generate-quiz/ai-generate-quiz.component';
import { CreateQuizComponent } from './modules/instructor/create-quiz/create-quiz.component';
import { StudentCoursesComponent } from './modules/student/student-courses/student-courses.component';
import { LoginComponent } from './modules/login/login.component';
import { InstructorDashboardComponent } from './modules/instructor/instructor-dashboard/instructor-dashboard.component';
const routes: Routes = [
  { path: '', component: CreateQuizComponent},
  { path: 'ai-generate-quiz', component: AiGenerateQuizComponent },
  { path: 'create-quiz', component: CreateQuizComponent},
  { path: 'student-courses', component: StudentCoursesComponent },
  { path: 'login', component:LoginComponent},
  { path: 'instructor-dashboard', component:InstructorDashboardComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
