import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { StudentDashboardComponent } from './student-dashboard/student-dashboard.component';
import { QuizComponent } from './quiz/quiz.component';
import { StudentCoursesComponent } from './student-courses/student-courses.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NavbarComponent } from 'src/app/components/navbar/navbar.component';
import { AuthInterceptor } from 'src/app/iterceptors/auth.interceptor';
import { SearchService } from 'src/app/services/search/search.service';
import { SearchResultComponent } from './search-result/search-result.component';
import { ToastrModule } from 'ngx-toastr';
import { MatSnackBarModule } from '@angular/material/snack-bar';

@NgModule({
  declarations: [
    StudentDashboardComponent,
    QuizComponent,
    StudentCoursesComponent,
    NavbarComponent,
    SearchResultComponent,
  ],
  imports: [
    CommonModule,
    MatProgressSpinnerModule,
    FormsModule,
    ToastrModule.forRoot({
      positionClass: 'toast-top-right', // Position of the toast
      timeOut: 3000, // Duration in milliseconds
      preventDuplicates: true, // Prevent duplicate toasts
    }),
    MatSnackBarModule,
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    SearchService
  ],
  exports: [StudentDashboardComponent,
    StudentCoursesComponent]
})
export class StudentModule { }
