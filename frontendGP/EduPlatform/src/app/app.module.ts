import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './components/navbar/navbar.component';
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

@NgModule({
  declarations: [
    
    AppComponent,
    NavbarComponent,
    FooterComponent,
    SidebarComponent,
    LoginComponent,
    CourseDetailsComponent,
    CourseDetailsComponent,
    AnnouncementComponent


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
    BrowserAnimationsModule

  ],
  providers: [EnrolledCoursesService,AuthService],
  bootstrap: [AppComponent]
})
export class AppModule { }
