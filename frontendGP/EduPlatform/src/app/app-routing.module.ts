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
import { ProctoredVideoComponent } from './proctored-video/proctored-video.component';
import { McqQuizViewerInstructorComponent } from './modules/instructor/mcq-quiz-viewer-instructor/mcq-quiz-viewer-instructor.component';
import { SearchResultInstructorComponent } from './modules/instructor/search-result-instructor/search-result-instructor.component';
import { StreamComponent } from './modules/instructor/stream/stream.component';
import { AuthGuard } from './modules/login/auth.guard';
import { SubmittedQuizzesComponent } from './modules/student/submitted-quizzes/submitted-quizzes.component';
import { UpcommingQuizzesComponent } from './modules/student/upcomming-quizzes/upcomming-quizzes.component';
import { TakeQuizErrorComponent } from './modules/student/quiz-already-submitted-error/take-quiz-error/take-quiz-error.component';
import { AssignmentSubmissionsComponent } from './modules/instructor/assignment-submissions/assignment-submissions.component';
import { QuizSubmissionsComponent } from './modules/instructor/quiz-submissions/quiz-submissions.component';

const routes: Routes = [
  { path: '', component: HomepageComponent },
  { path: 'ai-generate-quiz', component: AiGenerateQuizComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'create-quiz', component: CreateQuizComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'student-courses', component: StudentCoursesComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_STUDENT' } },
  { path: 'login', component: LoginComponent },
  { path: 'instructor-dashboard', component: InstructorDashboardComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'course-details/:id', component: CourseDetailsComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_STUDENT' } },
  { path: 'searchResult', component: SearchResultComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_STUDENT' } },
  { path: 'create-course', component: CreateCourseComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'instructor-courses', component: CoursesComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'searchResult-Instructor', component: SearchResultInstructorComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'stream/:id', component: StreamComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'manual-mcq-quiz', component: ManualMcqQuizComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'manual-essay-quiz', component: ManualEssayQuizComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'take-quiz/:courseId/:quizId', component: QuizDetailsComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_STUDENT' } },
  { path: 'video-player', component: ProctoredVideoComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_STUDENT' } },
  { path: 'mcq-quiz-viewer/:quizId', component: McqQuizViewerInstructorComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: '', redirectTo: 'homepage', pathMatch: 'full' },
  { path: 'instructor/quiz-viewer/:quizId', component: McqQuizViewerInstructorComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'instructor-dashboard', component: InstructorDashboardComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'admin-dashboard', component: AdminDashboardComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_ADMIN' } },
  { path: 'admin-profile', component: AdminProfileComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_ADMIN' } },
  { path: 'users-profile', component: UsersProfileComponent, canActivate: [AuthGuard], },
  { path: 'assignment-submissions-instructor/:id', component: AssignmentSubmissionsComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' } },
  { path: 'submitted-quizzes', component: SubmittedQuizzesComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_STUDENT' } },
  { path: 'upcoming-quizzes', component: UpcommingQuizzesComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_STUDENT' } },
  { path: 'take-quiz-error', component: TakeQuizErrorComponent, canActivate: [AuthGuard], data: { expectedRole: 'ROLE_STUDENT' } },
  { path: 'quiz-submissions/:quizId', component: QuizSubmissionsComponent , canActivate: [AuthGuard], data: { expectedRole: 'ROLE_INSTRUCTOR' }  },

];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
