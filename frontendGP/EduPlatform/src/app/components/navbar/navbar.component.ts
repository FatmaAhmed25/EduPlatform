import { Component, OnInit } from '@angular/core';
import { WebSocketService } from 'src/app/services/websocket-service/websocket.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { SearchService } from 'src/app/services/search/search.service';
import { Router } from '@angular/router';
import { catchError, Observable, of, switchMap } from 'rxjs';
import { AnnouncementService } from 'src/app/services/announcement/announcement.service';
import { Course } from 'src/app/services/search/search.service';
import { SearchInstructorService } from 'src/app/services/searchInstructor/search-instructor.service';
import { Courses } from 'src/app/models/course.model';
interface Notification {
  id: number;
  notificationMessage: string;
}

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  courseIds: number[] = [];
  notifications: Notification[] = []; 
  notificationCount: number = 0;
  showNotificationPanel = false; 
  showDropdown = false;
  showSearchPanel = false;
  searchQuery: string = '';
  searchResults: Course[] = [];
  userCache: Map<string, string> = new Map();
  userName$: Observable<string>|null=null;
  showFilterOptions = false;
  searchType: 'code' | 'title' = 'code'; 
  userTypee = localStorage.getItem('userType');

  constructor(
    private webSocketService: WebSocketService,
    private searchService: SearchService,
    private http: HttpClient,
    private router: Router,
    private announcementService: AnnouncementService,
    private searchInstructorService: SearchInstructorService
  ) {}

  ngOnInit(): void {
    this.fetchEnrolledCourseIds();
    const userID = localStorage.getItem('userID');
    if(userID){
      this.userName$ = this.getUsername(userID);
    }
  }

  fetchEnrolledCourseIds(): void {
    const userID = localStorage.getItem('userID');
    const authToken = localStorage.getItem('authToken');
    
    if (userID && authToken) {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${authToken}`);
      this.http.get<Course[]>(`http://localhost:8080/students/enrolled-courses/${userID}`, { headers }).subscribe(
        courses => {
          this.courseIds = courses.map(course => course.courseId);
          this.subscribeToNotifications();
        },
        error => {
          console.error('Error fetching course IDs:', error);
        }
      );
    } else {
      console.error('userID or authToken is missing.');
    }
  }

  toggleFilterOptions(): void {
    this.showFilterOptions = !this.showFilterOptions;
  }

  subscribeToNotifications(): void {
    const token = localStorage.getItem('authToken');
    
    if (token) {
      this.webSocketService.connect(token, () => {
        console.log("WebSocket connected");

        this.courseIds.forEach(courseId => {
          this.webSocketService.subscribeToNotificationsForCourse(courseId, (message) => {
            console.log(`Received notification for course ${courseId}:`, message);
            this.notifications.unshift(message);
            this.notificationCount++;
          });
        });
      });
    }
  }

  toggleNotificationPanel(): void {
    this.showNotificationPanel = !this.showNotificationPanel;
    if (this.showNotificationPanel) {
      this.notificationCount = 0;
    }
    this.showDropdown = false;
    this.showSearchPanel = false;
  }

  toggleDropdown() {
    this.showDropdown = !this.showDropdown;
    this.showNotificationPanel = false;
    this.showSearchPanel = false;
  }

  toggleSearchPanel(): void {
    this.showSearchPanel = !this.showSearchPanel;
    this.showDropdown = false;
    this.showNotificationPanel = false;
  }
  searchCourses(): void {
    const userType = localStorage.getItem('userType');
    console.log('User Type:', userType); // Check user type
    console.log('Search Query:', this.searchQuery); // Check search query
    console.log('Search Type:', this.searchType); // Check search type
  
    if (userType === 'ROLE_STUDENT') {
      if (this.searchType === 'code') {
        console.log('Searching by code:', this.searchQuery);
        this.searchService.searchByCode(this.searchQuery).subscribe(
          () => {
            this.router.navigate(['/searchResult']);
          },
          error => console.error('Error searching courses by code:', error)
        );
      } else if (this.searchType === 'title') {
        console.log('Searching by title:', this.searchQuery);
        this.searchService.searchByTitle(this.searchQuery).subscribe(
          () => {
            this.router.navigate(['/searchResult']);
          },
          error => console.error('Error searching courses by title:', error)
        );
      }
    } else if (userType === 'ROLE_INSTRUCTOR') {
      const userID = localStorage.getItem('userID');
      if (userID) {
        this.searchInstructorService.getCoursesForInstructor(userID).subscribe(
          (courses: Courses[]) => {
            console.log('Instructor courses:', courses);
            this.searchInstructorService.getCoursesForInstructor(userID);
      });
      }
    }
  }
  
  
  getUsername(userId2: string): Observable<string> {
    if (this.userCache.has(userId2)) {
      return of(this.userCache.get(userId2) as string);
    } else {
      return this.announcementService.getUserDetails(userId2).pipe(
        switchMap((user) => {
          this.userCache.set(userId2, user.username);
          return of(user.username);
        }),
        catchError(() => of('Unknown User'))
      );
    }
  }

  goToProfile() {
    // Implement your profile navigation logic here
    this.router.navigate(['/student-profile']);
  }

  goToSettings() {
    // Implement your settings navigation logic here
  }
}
