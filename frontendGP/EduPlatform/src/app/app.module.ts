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

@NgModule({
  declarations: [
    
    AppComponent,
    NavbarComponent,
    FooterComponent,
    SidebarComponent,
    LoginComponent,
    

  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule,
    CoreModule,
    InstructorModule,
    StudentModule
  ],
  providers: [EnrolledCoursesService,AuthService],
  bootstrap: [AppComponent]
})
export class AppModule { }
