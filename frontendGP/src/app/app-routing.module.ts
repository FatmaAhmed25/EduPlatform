import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CoursesStudentComponent } from './courses-student/courses-student.component';
const routes: Routes = [
  {path:'coursesStudent',component:CoursesStudentComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
