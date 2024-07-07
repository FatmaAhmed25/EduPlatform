import { Component, OnInit } from '@angular/core';
import { WebSocketService } from 'src/app/services/websocket-service/websocket.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { SearchService } from 'src/app/services/search/search.service';
import { Router } from '@angular/router';
interface Notification {
  id: number;
  notificationMessage: string;
}

interface Course {
  courseId: number;
  courseCode: string;
  title: string;
  description: string;
}

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  courseIds: number[] = []; // Array to store enrolled course IDs
  notifications: Notification[] = []; // Array to store notifications
  notificationCount: number = 0; // Notification count property
  showNotificationPanel = false; // Toggle for showing notification panel
  showDropdown = false;
  showSearchPanel = false;
  searchQuery: string = '';
  searchResults: Course[] = [];
  constructor(
    private webSocketService: WebSocketService,
    private searchService: SearchService,
    private http: HttpClient,
    private router:Router
  ) {}

  ngOnInit(): void {
    this.fetchEnrolledCourseIds();
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

  subscribeToNotifications(): void {
    const token = localStorage.getItem('authToken');
    
    if (token) {
      this.webSocketService.connect(token, () => {
        console.log("WebSocket connected");

        this.courseIds.forEach(courseId => {
          this.webSocketService.subscribeToNotificationsForCourse(courseId, (message) => {
            console.log(`Received notification for course ${courseId}:`, message);
            // Add new notification to the beginning of the array
            this.notifications.unshift(message);
            // Increment notification count
            this.notificationCount++;
            // this.showNotificationPanel = true;
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
  
  searchCoursesByCode(): void {
    const userType = localStorage.getItem('userType');
    console.log(userType +" navbar")
    this.searchService.searchByCode(this.searchQuery).subscribe(
      () => {
        if(userType==='ROLE_STUDENT'){
        this.router.navigate(['/searchResult']);
        }else{
          this.router.navigate(['/searchResult-Instructor'])
        }
      },
      error => console.error('Error searching courses by code:', error)
    );
  }

  searchCoursesByTitle(): void {
    const userType = localStorage.getItem('userType');
    console.log(userType +" navbar")
    this.searchService.searchByTitle(this.searchQuery).subscribe(
      () => {
        if(userType==='ROLE_STUDENT'){
          this.router.navigate(['/searchResult']);
          }else{
            this.router.navigate(['/searchResult-Instructor'])
          }
      },
      error => console.error('Error searching courses by title:', error)
    );
  }

  goToProfile() {
    this.router.navigate(['/student-profile']);
  }

  goToSettings() {
    // Logic to navigate to settings
  }
}