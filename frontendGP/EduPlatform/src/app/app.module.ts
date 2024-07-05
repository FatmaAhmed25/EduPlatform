import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
// import { CommonModule } from '@angular/common';
import { FooterComponent } from './components/footer/footer.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { LoginComponent } from './modules/login/login.component';
import { CoreModule } from './core/core.module';
import { InstructorModule } from './modules/instructor/instructor.module';
import { StudentModule } from './modules/student/student.module';
import { FormsModule } from '@angular/forms';
import { HttpClientModule,HTTP_INTERCEPTORS } from '@angular/common/http';
import { EnrolledCoursesService } from 'src/app/services/student-course/enrolled-courses.service';
import { AuthService } from './services/authService/auth.service';
import { CourseDetailsComponent } from 'src/app/modules/student/course-details/course-details.component';
import { MatTabsModule } from '@angular/material/tabs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AnnouncementComponent } from 'src/app/modules/student/announcement/announcement.component';
import { SpinnerComponent } from './utils/spinner/spinner.component';
import { SharedModule } from './shared/shared.module';
import { FileViewerDialogComponent } from 'src/app/file-viewer-dialog/file-viewer-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { SafeUrlPipe } from 'src/app/file-viewer-dialog/safe-url.pipe';
import { WebSocketService } from 'src/app/services/websocket-service/websocket.service';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { HomepageComponent } from './homepage/homepage.component';
import { LectureService } from 'src/app/services/lectureService/lecture.service';
import { LectureComponent } from 'src/app/modules/student/lecture/lecture.component';
import { LabService } from 'src/app/services/Lab/lab.service';
import { LabComponent } from 'src/app/modules/student/lab/lab.component';
import { SearchService } from 'src/app/services/search/search.service';
import { AuthInterceptor } from './iterceptors/auth.interceptor';
import { ToastrModule } from 'ngx-toastr';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { AssignmentService } from 'src/app/services/assignementService/assignement.service';
import { AssignmentComponent } from 'src/app/modules/student/assignment/assignment.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input'; // Needed for the input element
import { SubmitDialogComponent } from 'src/app/modules/student/submit-dialog/submit-dialog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; // Add this import
import { QuizzesComponent } from 'src/app/modules/student/quizzes/quizzes.component';
import { QuizService } from 'src/app/services/quizService/quiz.service';
// import { CreateQuizComponent } from 'src/app/modules//create-quiz/create-quiz.component';
import { NavbarComponent } from 'src/app/components/navbar/navbar.component';
import { MatSelectModule } from '@angular/material/select';

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    SidebarComponent,
    LoginComponent,
    CourseDetailsComponent,
    CourseDetailsComponent,
    SpinnerComponent,
    FileViewerDialogComponent,  
    SafeUrlPipe,
    AnnouncementComponent,
    HomepageComponent,LectureComponent,LabComponent,AssignmentComponent,SubmitDialogComponent,QuizzesComponent,
    NavbarComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    MatTabsModule,
    HttpClientModule,
    AppRoutingModule,
    CoreModule,
    InstructorModule,
    StudentModule,
    BrowserAnimationsModule,
    MatDialogModule,
    SharedModule,
    MatIconModule,
    RouterModule,MatFormFieldModule,MatInputModule,MatButtonModule,MatProgressSpinnerModule,
    ToastrModule.forRoot({
      positionClass: 'toast-top-right', // Position of the toast
      timeOut: 3000, // Duration in milliseconds
      preventDuplicates: true, // Prevent duplicate toasts
    }),
    MatSnackBarModule,
    MatSelectModule,

  ],
  providers: [EnrolledCoursesService,AuthService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    WebSocketService,LectureService,LabService,QuizService,SearchService,AssignmentService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
