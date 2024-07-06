import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AdminService } from '../services/admin.service';
import { UserDTO } from '../models/dtos/usersdto';
import { UserPopupComponent } from '../user-popup/user-popup.component';
import { MatDialog } from '@angular/material/dialog';
import { ImportDialogComponent } from '../import-dialog/import-dialog.component';

@Component({
  selector: 'app-users-profile',
  templateUrl: './users-profile.component.html',
  styleUrls: ['./users-profile.component.scss']
})
export class UsersProfileComponent implements OnInit {

  otherUserProfile: any = {};
  students: UserDTO[] = [];
  instructors: UserDTO[] = [];
  userInfo: UserDTO | null = null;
  updateUserId: number = 0;
  userId: number | null = null;
  searchTerm: string = '';
  showPassword: boolean[] = [];
  currentPage: number = 1;
  usersPerPage: number = 5;
  searchResults: UserDTO[] = [];
  student: any = {};
  instructor: any = {};
  studentFile: File | null = null;
  instructorFile: File | null = null;

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  importUserType: 'student' | 'instructor' | null = null;

  constructor(private adminService: AdminService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.getAllStudents();
    this.getAllInstructors();
  }

  // Method to show import dialog
  showImportDialog(): void {
    const dialogRef = this.dialog.open(ImportDialogComponent, {
      width: '400px',
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const { userType, file } = result;
        if (userType === 'student') {
          this.importStudents(file);
        } else if (userType === 'instructor') {
          this.importInstructors(file);
        }
      }
    });
  }

  importStudents(file: File): void {
    this.adminService.importStudents(file).subscribe(
      response => {
        console.log('Students imported:', response);
        this.getAllStudents();
      },
      error => {
        console.error('Importing students failed:', error);
      }
    );
  }

  importInstructors(file: File): void {
    this.adminService.importInstructors(file).subscribe(
      response => {
        console.log('Instructors imported:', response);
        this.getAllInstructors();
      },
      error => {
        console.error('Importing instructors failed:', error);
      }
    );
  }

  getAllStudents(): void {
    this.adminService.getAllStudents().subscribe(
      students => {
        this.students = students;
        this.showPassword = new Array(students.length).fill(false);
        this.triggerSearch(this.searchTerm); // Trigger search after fetching students
      },
      error => console.error('Error fetching students:', error)
    );
  }

  getAllInstructors(): void {
    this.adminService.getAllInstructors().subscribe(
      instructors => {
        this.instructors = instructors;
        this.showPassword = new Array(instructors.length).fill(false);
        this.triggerSearch(this.searchTerm); // Trigger search after fetching instructors
      },
      error => console.error('Error fetching instructors:', error)
    );
  }

  getUserById(): void {
    if (this.userId !== null) {
      this.adminService.getUserById(this.userId).subscribe(
        userInfo => this.userInfo = userInfo,
        error => console.error('Error fetching user info:', error)
      );
    }
  }

  editUser(user: UserDTO): void {
    const { userId, userType, ...updatedFields } = user;
    this.adminService.updateOtherUserProfile({ userId, ...updatedFields }).subscribe(
      response => {
        console.log('User profile updated:', response);
        user.editing = false;
      },
      error => {
        console.error('Updating user profile failed:', error);
      }
    );
  }

  deleteUser(userId: number): void {
    // Logic to delete user
  }

  triggerSearch(searchTerm: string): void {
    this.adminService.searchUsers(this.searchTerm).subscribe(
      results => {
        this.searchResults = results;
      },
      error => console.error('Error searching users:', error)
    );
  }

  filteredUsers(): UserDTO[] {
    if (this.searchTerm.trim() === '') {
      return [...this.students, ...this.instructors];
    } else {
      return this.searchResults;
    }
  }

  paginatedUsers(): UserDTO[] {
    const startIndex = (this.currentPage - 1) * this.usersPerPage;
    return this.filteredUsers().slice(startIndex, startIndex + this.usersPerPage);
  }

  totalPages(): number {
    return Math.ceil(this.filteredUsers().length / this.usersPerPage);
  }

  totalPagesArray(): number[] {
    return Array(this.totalPages()).fill(0).map((x, i) => i + 1);
  }

  goToPage(page: number): void {
    this.currentPage = page;
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages()) {
      this.currentPage++;
    }
  }

  toggleShowPassword(index: number): void {
    const globalIndex = (this.currentPage - 1) * this.usersPerPage + index;
    this.showPassword[globalIndex] = !this.showPassword[globalIndex];
  }

  addNewUser(): void {
    const dialogRef = this.dialog.open(UserPopupComponent, {
      width: '400px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (result.userType === 'student') {
          this.createStudent(result); 
          this.getAllStudents();
        } else if (result.userType === 'instructor') {
          this.createInstructor(result); 
          this.getAllInstructors();
        }
      }
    });
  }

  createStudent(newStudent: UserDTO) {
    this.adminService.createStudent(newStudent).subscribe(
      response => {
        console.log('Student created:', response);
        this.getAllStudents();
      },
      error => {
        console.error('Creating student failed:', error);
      }
    );
  }

  createInstructor(newInstructor: UserDTO) {
    this.adminService.createInstructor(newInstructor).subscribe(
      response => {
        console.log('Instructor created:', response);
        this.getAllInstructors();
      },
      error => {
        console.error('Creating instructor failed:', error);
      }
    );
  }


}
