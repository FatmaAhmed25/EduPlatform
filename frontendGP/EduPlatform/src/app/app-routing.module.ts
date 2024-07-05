import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AiGenerateQuizComponent } from './modules/instructor/ai-generate-quiz/ai-generate-quiz.component';
import { CreateQuizComponent } from './modules/instructor/create-quiz/create-quiz.component';
import { StudentCoursesComponent } from './modules/student/student-courses/student-courses.component';
import { LoginComponent } from './modules/login/login.component';
import { InstructorDashboardComponent } from './modules/instructor/instructor-dashboard/instructor-dashboard.component';
import { CourseDetailsComponent } from 'src/app/modules/student/course-details/course-details.component';
import { HomepageComponent } from './homepage/homepage.component';
import { SearchResultComponent } from './modules/student/search-result/search-result.component';
import { CreateCourseComponent } from './modules/instructor/create-course/create-course.component';
import { CoursesComponent } from './modules/instructor/courses/courses.component';
import { ManualMcqQuizComponent } from './modules/instructor/manual-MCQ-quiz/manual-MCQ-quiz.component';
import { ManualEssayQuizComponent } from 'src/app/modules/instructor/manual-essay-quiz/manual-essay-quiz.component';


const routes: Routes = [
  { path: '', component: HomepageComponent},
  { path: 'ai-generate-quiz', component: AiGenerateQuizComponent },
  { path: 'create-quiz', component: CreateQuizComponent},
  { path: 'student-courses', component: StudentCoursesComponent },
  { path: 'login', component:LoginComponent},
  { path: 'instructor-dashboard', component:InstructorDashboardComponent},
  { path: 'course-details/:id', component: CourseDetailsComponent },
  { path: 'searchResult', component: SearchResultComponent },
  { path: 'create-course', component: CreateCourseComponent },
  { path: 'instructor-courses', component: CoursesComponent },
  { path: 'manual-mcq-quiz', component: ManualMcqQuizComponent },
  { path: 'manual-essay-quiz', component: ManualEssayQuizComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
