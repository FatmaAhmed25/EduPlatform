import { Component, OnInit } from '@angular/core';
import { AdminService } from '../services/admin.service';
import { HttpErrorResponse } from '@angular/common/http';
import { UserDTO } from '../models/dtos/usersdto';

@Component({
  selector: 'app-admin-profile',
  templateUrl: './admin-profile.component.html',
  styleUrls: ['./admin-profile.component.scss']
})
export class AdminProfileComponent implements OnInit {


  userId: number | null = null; // Initialize userId here
  adminProfile: any = {};
  userInfo: UserDTO | null = null;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.userId = this.getUserIdFromLocalStorage();
    this.getUserById();
    console.log(this.getUserById())

  }

  getUserIdFromLocalStorage(): number | null {
    console.log(localStorage.getItem('userID'))
    const userId = localStorage.getItem('userID');
    return userId ? +userId : null; // Convert to number or return null if not found
  }

  updateAdminProfile(): void {
    this.adminProfile.userId =this.getUserIdFromLocalStorage();

    this.adminService.updateAdminProfile(this.adminProfile).subscribe(
      (response) => {
        console.log('Profile updated successfully:', response);
       
      },
      (error: HttpErrorResponse) => {
   
        console.error('Error updating profile:', error);
       
      }
    );

  }

  getUserById(): void {
    if (this.userId !== null) {
      this.adminService.getUserById(this.userId).subscribe(
        userInfo => {
          this.userInfo = userInfo; // Store fetched userInfo
          this.adminProfile = { ...userInfo }; // Populate adminProfile for editing
        },
        error => console.error('Error fetching user info:', error)
      );
    }
  }


}
