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
import { HttpClientModule } from '@angular/common/http';
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
    HomepageComponent,LectureComponent

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
    RouterModule
  ],
  providers: [EnrolledCoursesService,AuthService,
    WebSocketService,LectureService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
