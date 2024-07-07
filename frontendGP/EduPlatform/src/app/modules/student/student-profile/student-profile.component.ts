import { Component, OnInit } from '@angular/core';
import { StudentDetailsService } from 'src/app/services/student-details/student-details.service';
import { HttpErrorResponse } from '@angular/common/http';
import { UserDTO } from 'src/app/models/dtos/usersdto';

@Component({
  selector: 'app-student-profile',
  templateUrl: './student-profile.component.html',
  styleUrls: ['./student-profile.component.scss']
})
export class StudentProfileComponent implements OnInit {
  userId: number | null = null;
  studentProfile: any = { email: '', username: '', currentPassword: '', newPassword: '', bio: '' };
  userInfo: UserDTO | null = null;

  constructor(private studentDetailsService: StudentDetailsService) {}

  ngOnInit(): void {
    this.userId = this.getUserIdFromLocalStorage();
    this.getUserById();
  }

  getUserIdFromLocalStorage(): number | null {
    const userId = localStorage.getItem('userID');
    return userId ? +userId : null; // Convert to number or return null if not found
  }

  updateAdminProfile(): void {
    if (this.userId !== null) {
      this.studentProfile.userId = this.userId;

      if (this.studentProfile.newPassword && !this.studentProfile.currentPassword) {
        alert('Please provide the current password to change your password.');
        return;
      }

      this.studentDetailsService.updateStudentProfile(this.studentProfile).subscribe(
        response => {
          console.log('Profile updated successfully:', response);
        },
        (error: HttpErrorResponse) => {
          console.error('Error updating profile:', error);
        }
      );
    }
  }

  getUserById(): void {
    if (this.userId !== null) {
      this.studentDetailsService.getStudentDetails(this.userId).subscribe(
        userInfo => {
          this.userInfo = userInfo; // Store fetched userInfo
          this.studentProfile = { ...userInfo }; // Populate studentProfile for editing
        },
        error => console.error('Error fetching user info:', error)
      );
    }
  }
}
