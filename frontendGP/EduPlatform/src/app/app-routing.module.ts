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
import { AdminDashboardComponent } from './modules/admin/admin-dashboard/admin-dashboard.component';
import { AdminProfileComponent } from './admin-profile/admin-profile.component';
import { UsersProfileComponent } from './users-profile/users-profile.component';
import { ManualMcqQuizComponent } from './modules/instructor/manual-MCQ-quiz/manual-MCQ-quiz.component';
import { ManualEssayQuizComponent } from 'src/app/modules/instructor/manual-essay-quiz/manual-essay-quiz.component';
import { QuizDetailsComponent } from './modules/student/quiz-details/quiz-details.component';
import {ProctoredVideoComponent} from './proctored-video/proctored-video.component'
import { McqQuizViewerInstructorComponent } from './modules/instructor/mcq-quiz-viewer-instructor/mcq-quiz-viewer-instructor.component';
import { SearchResultInstructorComponent } from './modules/instructor/search-result-instructor/search-result-instructor.component';
import { StreamComponent } from './modules/instructor/stream/stream.component';
import { AuthGuard } from './modules/login/auth.guard';
import { SubmittedQuizzesComponent } from './modules/student/submitted-quizzes/submitted-quizzes.component';
import { UpcommingQuizzesComponent } from './modules/student/upcomming-quizzes/upcomming-quizzes.component';
import { TakeQuizErrorComponent } from './modules/student/quiz-already-submitted-error/take-quiz-error/take-quiz-error.component';

import { AssignmentSubmissionsComponent } from './modules/instructor/assignment-submissions/assignment-submissions.component';
const routes: Routes = [
  { path: '', component: HomepageComponent},
  { path: 'ai-generate-quiz', component: AiGenerateQuizComponent,canActivate: [AuthGuard] },
  { path: 'create-quiz', component: CreateQuizComponent, canActivate: [AuthGuard]},
  { path: 'student-courses', component: StudentCoursesComponent , canActivate: [AuthGuard]},
  { path: 'login', component:LoginComponent},
  { path: 'instructor-dashboard', component:InstructorDashboardComponent, canActivate: [AuthGuard]},
  { path: 'course-details/:id', component: CourseDetailsComponent, canActivate: [AuthGuard] },
  { path: 'searchResult', component: SearchResultComponent , canActivate: [AuthGuard]},
  { path: 'create-course', component: CreateCourseComponent, canActivate: [AuthGuard] },
  { path: 'instructor-courses', component: CoursesComponent, canActivate: [AuthGuard] },
  { path: 'searchResult-Instructor', component: SearchResultInstructorComponent, canActivate: [AuthGuard] },
  { path: 'stream/:id', component: StreamComponent , canActivate: [AuthGuard]},
  { path: 'manual-mcq-quiz', component: ManualMcqQuizComponent , canActivate: [AuthGuard]},
  { path: 'manual-essay-quiz', component: ManualEssayQuizComponent , canActivate: [AuthGuard]} ,
  { path: 'take-quiz/:courseId/:quizId', component: QuizDetailsComponent, canActivate: [AuthGuard] },
  { path: 'video-player', component: ProctoredVideoComponent, canActivate: [AuthGuard] },
  { path: 'mcq-quiz-viewer/:quizId', component: McqQuizViewerInstructorComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: 'homepage', pathMatch: 'full' },
  { path: 'instructor/quiz-viewer/:quizId', component: McqQuizViewerInstructorComponent,canActivate: [AuthGuard] },
  { path: 'instructor-dashboard', component:InstructorDashboardComponent},
  {path:'admin-dashboard',component:AdminDashboardComponent},
  {path:'admin-profile',component:AdminProfileComponent,canActivate: [AuthGuard]},
  {path:'users-profile',component:UsersProfileComponent,canActivate: [AuthGuard]},
  {path:'assignment-submissions-instructor/:id/:CourseId',component:AssignmentSubmissionsComponent,canActivate: [AuthGuard]},
  {path:'submitted-quizzes',component:SubmittedQuizzesComponent},
  {path: 'upcoming-quizzes',component:UpcommingQuizzesComponent},
  { path: 'take-quiz-error', component: TakeQuizErrorComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
