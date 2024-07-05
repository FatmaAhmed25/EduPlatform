import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { WebSocketService } from 'src/app/services/websocket-service/websocket.service';
import { FileViewerDialogComponent } from 'src/app/file-viewer-dialog/file-viewer-dialog.component';
import { StudentDetailsDialogComponent } from '../student-details-dialog/student-details-dialog.component';
import { AnnouncementDialogComponent } from '../announcement-dialog/announcement-dialog.component';
import { Student } from 'src/app/models/Student.model';
import { Clipboard } from '@angular/cdk/clipboard';
import { StreamService } from 'src/app/services/stream/stream.service';
import { StompSubscription } from '@stomp/stompjs';
import { AnnouncementService } from 'src/app/services/announcementDialog/announcement.service';
import { Instructor } from 'src/app/models/instructor.model';
import { Courses } from 'src/app/models/course.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LecturesComponent } from '../lectures/lectures.component';

@Component({
  selector: 'app-stream',
  templateUrl: './stream.component.html',
  styleUrls: ['./stream.component.scss']
})
export class StreamComponent implements OnInit, OnDestroy {
  students: Student[] = [];
  announcements: any[] = [];
  lectures: any[] = [];
  videos: any[] = []; // Added for videos
  newComment: { [key: number]: string } = {};
  commentSubscriptions: Map<number, any> = new Map();
  userCache: Map<string, string> = new Map(); // Cache for user data
  courseId: number | undefined;
  instructor: Instructor | undefined; 
  course: Courses | undefined; 
  link2:string='';
  constructor(
    private route: ActivatedRoute,
    private announcementService: AnnouncementService,
    public dialog: MatDialog,
    private webSocketService: WebSocketService,
    private stream: StreamService,
    private clipboard: Clipboard,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseId = +params['id'];
      if (this.courseId) {
        this.loadStudents();
        this.loadAnnouncements();
        this.loadCourseAndInstructorDetails();
        this.loadLecturesAndVideos(); // Modified to load both lectures and videos
      }
    });

    const token = localStorage.getItem('authToken');
    if (token) {
      this.webSocketService.connect(token, () => {
        console.log('WebSocket connected.');
        this.subscribeToAnnouncementTopics();
      });
    }
  }
  fetchAnnouncementsAndSetupWebSocket(): void {
    if (this.courseId) {
      this.stream.getAnnouncementsByCourseId(this.courseId).subscribe(data => {
        this.announcements = data;
        this.setupWebSocket();
      });
    }
  }
  setupWebSocket(): void {
    const token = localStorage.getItem('authToken');
    if (token) {
      this.webSocketService.connect(token, () => {
        console.log('WebSocket connected.');
        this.subscribeToAnnouncementTopics();
      });
    }
  }
  
  copyCourseCode(): void {
    if (this.course?.courseCode) {
      this.clipboard.copy(this.course.courseCode);
      console.log('Course code copied to clipboard:', this.course.courseCode);
      this.showCopyMessage();
    }
  }

  loadLecturesAndVideos(): void {
    const instructorId = localStorage.getItem('userID');
    if (this.courseId && instructorId) {
      this.stream.getLectures(this.courseId, instructorId).subscribe(
        (lectures) => {
          this.lectures = lectures;
        },
        (error) => {
          console.error('Error loading lectures:', error);
        }
      );
      this.stream.getVideos(this.courseId, instructorId).subscribe(
        (videos) => {
          this.videos = videos;
          console.log();
        },
        (error) => {
          console.error('Error loading videos:', error);
        }
      );
    }
  }

  copyCoursePassword(): void {
    if (this.course?.courseCode) {
      this.clipboard.copy(this.course.courseCode);
      console.log('Course Password copied to clipboard:', this.course.courseCode);
      this.showCopyMessage();
    }
  }

  showCopyMessage(): void {
    this.snackBar.open('Course code copied to clipboard', 'Close', {
      duration: 2000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['snackbar-success']
    });
  }

  ngOnDestroy(): void {
    this.commentSubscriptions.forEach(subscription => {
      if (subscription) {
        subscription.unsubscribe();
      }
    });
    this.webSocketService.disconnect();
  }

  loadStudents(): void {
    if (this.courseId) {
      this.stream.getStudentsByCourseId(this.courseId).subscribe((data: Student[]) => {
        this.students = data;
      });
    }
  }

  loadCourseAndInstructorDetails(): void {
    if (this.courseId) {
      this.announcementService.getCourseById(this.courseId).subscribe(course => {
        this.course = course;
        console.log('Course:', this.course);
      });

      const instructorId = localStorage.getItem('userID');
      if (instructorId) {
        this.announcementService.getInstructorDetails(instructorId).subscribe(instructor => {
          console.log('Instructor details:', instructor); // Log instructor details
          this.instructor = instructor;
        }, error => {
          console.error('Error fetching instructor details:', error);
        });
      }
    }
  }

  loadAnnouncements(): void {
    if (this.courseId) {
      console.log('Fetching announcements for course ID:', this.courseId);
      this.stream.getAnnouncementsByCourseId(this.courseId).subscribe(
        (data => {
          this.announcements = data;
          console.log('Announcements retrieved:', this.announcements);
          this.announcements.forEach(announcement => {
            this.subscribeToCommentUpdates(announcement.id);
          });
        }),
        error => {
          console.error('Error fetching announcements:', error);
        }
      );
    }
  }

  openAnnouncementDialog(): void {
    const instructorId = localStorage.getItem('userID');
    const dialogRef = this.dialog.open(AnnouncementDialogComponent, {
      width: '600px',
      data: { instructorId: instructorId, courseId: this.courseId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Announcement Posted:', result);
        this.announcements.push(result); 
      }
    });
  }

  openStudentDetails(): void {
    const dialogRef = this.dialog.open(StudentDetailsDialogComponent, {
      width: '500px',
      data: { students: this.students }
    });

    dialogRef.afterClosed().subscribe(() => {
      console.log('The dialog was closed');
    });
  }

  toggleComments(announcementId: number): void {
    const announcement = this.announcements.find(a => a.id === announcementId);
    if (announcement) {
      announcement.showComments = !announcement.showComments;
    }
  }

  sendComment(announcementId: number): void {
    const content = this.newComment[announcementId];
    const userId = localStorage.getItem("userID");

    const comment = {
      announcementId,
      userId,
      commentContent: content
    };

    this.webSocketService.send(`/app/announcement/${announcementId}/comments`, comment);
    this.newComment[announcementId] = '';
  }
  openAddMaterialDialog(): void {
    const instructorId = localStorage.getItem('userID');
    const dialogRef = this.dialog.open(LecturesComponent, {
      width: '600px',
      data: { courseId: this.courseId, instructorId: instructorId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Material Added:', result);
        this.loadLecturesAndVideos(); // Reload lectures and videos
      }
    });
  }
  openVideoLink(announcement:any):void{
    if (this.courseId && announcement.fileName) {
      this.announcementService.getFileLink(this.courseId, announcement.fileName).subscribe(link=>{
         this.link2=link.replace(/^"(.*)"$/, '$1');
         console.log(this.link2);
      })
  }
}
  openFileLink(announcement: any): void {
    if (this.courseId && announcement.fileName) {
      this.announcementService.getFileLink(this.courseId, announcement.fileName).subscribe(link => {
        const cleanedLink = link.replace(/^"(.*)"$/, '$1');

        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '80vw';
        dialogConfig.height = '80vh';
        dialogConfig.data = { fileUrl: cleanedLink, fileName: announcement.fileName };

        this.dialog.open(FileViewerDialogComponent, dialogConfig);
      });
    }
  }

  subscribeToAnnouncementTopics(): void {
    this.announcements.forEach(announcement => {
      this.fetchInitialComments(announcement.id);
      this.subscribeToCommentUpdates(announcement.id);
    });
  }
  fetchInitialComments(announcementId: number): void {
    console.log(`Calling getCommentsByAnnouncementId for announcement ID: ${announcementId}`); // Log before API call
    this.announcementService.getCommentsByAnnouncementId(announcementId).subscribe(comments => {
      console.log(`Received comments for announcement ID: ${announcementId}`, comments); // Log received comments
      const announcement = this.announcements.find(a => a.id === announcementId);
      if (announcement) {
        announcement.comments = comments;
      }
    });
  }

  subscribeToCommentUpdates(announcementId: number): void {
    const topic = `/topic/announcement/${announcementId}/comments`;
    console.log(`Subscribing to topic: ${topic}`);
    const subscription = this.webSocketService.subscribeToComments(topic, (message: any) => {
      console.log(`Received WebSocket message for announcement ID: ${announcementId}`, message);
      const announcement = this.announcements.find(a => a.id === announcementId);
      if (announcement) {
        announcement.comments.push(message);
      }
    });
    this.commentSubscriptions.set(announcementId, subscription);
  }

  getUsername(userId: string): Observable<string> {
    if (this.userCache.has(userId)) {
      return of(this.userCache.get(userId) as string);
    } else {
      return this.announcementService.getInstructorDetails(userId).pipe(
        switchMap(user => {
          this.userCache.set(userId, user.username);
          return of(user.username);
        }),
        catchError(() => of('Unknown User'))
      );
    }
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString();
  }

  showModal = false;
  newContent = { title: '', type: '', content: '' };

  openPublishModal() {
    this.showModal = true;
  }

  closePublishModal() {
    this.showModal = false;
  }

  publishContent() {
    // Handle the publish logic here
    this.closePublishModal();
  }
}
