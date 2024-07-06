import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { CourseDTO } from 'src/app/models/dtos/coursedto';
import { UserDTO } from 'src/app/models/dtos/usersdto';
import { AdminService } from 'src/app/services/admin.service';

@Component({
  selector: 'app-submitted-quizzes',
  templateUrl: './submitted-quizzes.component.html',
  styleUrls: ['./submitted-quizzes.component.scss']
})
export class SubmittedQuizzesComponent {

  // Models for data binding
  student: any = {};
  instructor: any = {};
  adminProfile: any = {};
  otherUserProfile: any = {};
  resetPasswordUserId: number = 0;
  resetPasswordNewPassword: string = '';
  updateUserId: number = 0;
  userId: number | null = null; // Initialize userId here
  numberOfUsers: number = 0;
  numberOfStudents: number = 0;
  numberOfInstructors: number = 0;
  numberOfCourses: number = 0;
  students: UserDTO[] = [];
  instructors: UserDTO[] = [];
  userInfo: UserDTO | null = null;
  searchUserId: number | null = null;
  courses: CourseDTO[] = [];



  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.userId = this.getUserIdFromLocalStorage();
    this.loadStatistics();
    this.getAllStudents();
    this.getAllInstructors();
    this.getAllCoursesWithDetails();
  }

  togglePasswordVisibility(course: any) {
    course.showPassword = !course.showPassword; // Toggle password visibility flag
}
  getUserIdFromLocalStorage(): number | null {
    const userId = localStorage.getItem('userID');
    return userId ? +userId : null; // Convert to number or return null if not found
  }


  // File selected event handler
  onFileSelected(event: any, type: string) {
    const file: File = event.target.files[0];
    if (type === 'student') {
      this.student.file = file;
    } else if (type === 'instructor') {
      this.instructor.file = file;
    }
  }

  // Import students from file
  importStudents() {
    if (this.student.file) {
      this.adminService.importStudents(this.student.file).subscribe(
        response => {
          console.log('Students imported:', response);
          // Handle success
        },
        error => {
          console.error('Importing students failed:', error);
          // Handle error
        }
      );
    }
  }

  // Import instructors from file
  importInstructors() {
    if (this.instructor.file) {
      this.adminService.importInstructors(this.instructor.file).subscribe(
        response => {
          console.log('Instructors imported:', response);
          // Handle success
        },
        error => {
          console.error('Importing instructors failed:', error);
          // Handle error
        }
      );
    }
  }

  // Create student
  createStudent() {
    this.adminService.createStudent(this.student).subscribe(
      response => {
        console.log('Student created:', response);
        // Handle success
      },
      error => {
        console.error('Creating student failed:', error);
        // Handle error
      }
    );
  }

  // Create instructor
  createInstructor() {
    this.adminService.createInstructor(this.instructor).subscribe(
      response => {
        console.log('Instructor created:', response);
        // Handle success
      },
      error => {
        console.error('Creating instructor failed:', error);
        // Handle error
      }
    );
  }

  // Reset password
  resetPassword() {
    this.adminService.resetPassword(this.resetPasswordUserId, this.resetPasswordNewPassword).subscribe(
      response => {
        console.log('Password reset:', response);
        // Handle success
      },
      error => {
        console.error('Resetting password failed:', error);
        // Handle error
      }
    );
  }

  updateAdminProfile(): void {
    this.adminProfile.userId =this.getUserIdFromLocalStorage();

    this.adminService.updateAdminProfile(this.adminProfile).subscribe(
      (response) => {
        console.log('Profile updated successfully:', response);
        // Handle success response if needed
      },
      (error: HttpErrorResponse) => {
   
        console.error('Error updating profile:', error);
        // Handle error response if needed
      }
    );

  }



  // Update other user profile
  updateOtherUserProfile() {
    this.otherUserProfile.userId = this.updateUserId;
    this.adminService.updateOtherUserProfile(this.otherUserProfile).subscribe(
      response => {
        console.log('User profile updated:', response);
        // Handle success
      },
      error => {
        console.error('Updating user profile failed:', error);
        // Handle error
      }
    );
  }


  loadStatistics(): void {
    this.adminService.getNumberOfUsers().subscribe(
      (response) => { this.numberOfUsers = response; },
      (error) => { console.error('Error fetching number of users:', error); }
    );
    
    this.adminService.getNumberOfStudents().subscribe(
      (response) => { this.numberOfStudents = response; },
      (error) => { console.error('Error fetching number of students:', error); }
    );
    
    this.adminService.getNumberOfInstructors().subscribe(
      (response) => { this.numberOfInstructors = response; },
      (error) => { console.error('Error fetching number of instructors:', error); }
    );
    
    this.adminService.getNumberOfCourses().subscribe(
      (response) => { this.numberOfCourses = response; },
      (error) => { console.error('Error fetching number of courses:', error); }
    );
  }

  getAllStudents(): void {
    this.adminService.getAllStudents().subscribe(
      students => this.students = students,
      error => console.error('Error fetching students:', error)
    );
  }

  getAllInstructors(): void {
    this.adminService.getAllInstructors().subscribe(
      instructors => this.instructors = instructors,
      error => console.error('Error fetching instructors:', error)
    );
  }

  getUserById(): void {
    if (this.searchUserId !== null) {
      this.adminService.getUserById(this.searchUserId).subscribe(
        userInfo => this.userInfo = userInfo,
        error => console.error('Error fetching user info:', error)
      );
    }
  }

  getAllCoursesWithDetails(): void {
    this.adminService.getAllCoursesWithDetails().subscribe(
        courses => {
            this.courses = courses;
            // Initialize showPassword property for each course
            this.courses.forEach(course => course.showPassword = false);
        },
        error => console.error('Error fetching courses:', error)
    );
}

}
