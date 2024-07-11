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
import { AnnouncementService } from 'src/app/services/announcementDialog/announcement.service';
import { Instructor } from 'src/app/models/instructor.model';
import { Courses } from 'src/app/models/course.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LecturesComponent } from '../lectures/lectures.component';
import { LabsComponent } from '../labs/labs.component';
import { AssignmentsComponent } from '../assignments/assignments.component';
import { Router } from '@angular/router';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';
import { AssignmentSubmissionsService } from 'src/app/services/assignmentSubmission/assignment-submissions.service';
@Component({
  selector: 'app-stream',
  templateUrl: './stream.component.html',
  animations: [
    trigger('pageAnimations', [
      transition(':enter', [
        query('.container, .left-panel, .main-content, .side-panel', [
          style({ opacity: 0, transform: 'translateY(-100px)' }),
          stagger(100, [
            animate(
              '500ms ease-out',
              style({ opacity: 1, transform: 'translateY(0)' })
            ),
          ]),
        ]),
      ]),
    ]),
  ],
  styleUrls: ['./stream.component.scss']
})
export class StreamComponent implements OnInit, OnDestroy {
  students: Student[] = [];
  announcements: any[] = [];
  lectures: any[] = [];
  videos: any[] = [];
  newComment: { [key: number]: string } = {};
  commentSubscriptions: Map<number, any> = new Map();
  userCache: Map<string, string> = new Map(); 
  courseId: number | undefined;
  instructor: Instructor | undefined; 
  course: Courses | undefined; 
  link2:string='';
  labs: any[] = [];
  assignments: any[]=[];
  quizzes: any[]=[];
  constructor(
    private route: ActivatedRoute,
    private announcementService: AnnouncementService,
    public dialog: MatDialog,
    private webSocketService: WebSocketService,
    private stream: StreamService,
    private clipboard: Clipboard,
    private snackBar: MatSnackBar,
    private router: Router 

  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseId = +params['id'];
      if (this.courseId) {
        this.loadStudents();
        this.loadAnnouncements();
        this.loadCourseAndInstructorDetails();
        this.loadLecturesAndVideos();
        this.loadLabs();
        this.loadAssignments();
        this.loadQuizzes();
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
  
  toggleDropdown(announcement: any): void {
    this.announcements.forEach(c => {
      if (c !== announcement) {
        c.showDropdown = false;
      }
    });
    announcement.showDropdown = !announcement.showDropdown;
  }
  Delete(announcementId : number):void{
    const instructorId = localStorage.getItem('userID');
    if(this.courseId && announcementId && instructorId){
    this.announcementService.deleteAnnouncement(this.courseId,instructorId,announcementId).subscribe(data => {
      this.snackBar.open('Announcement Deleted Successfully.', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right'
      });
      this.loadAnnouncements();
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
  loadLabs(): void {
    if (this.courseId) {
      this.stream.getLabs(this.courseId).subscribe(
        (labs) => {
          this.labs = labs;
        },
        (error) => {
          this.snackBar.open('Error loading labs', 'Close', {
            duration: 5000,
            verticalPosition: 'top',
            horizontalPosition: 'right'
          });
        }
      );
    }
  }
  loadAssignments():void{
    if(this.courseId){
      this.stream.getAssignments(this.courseId).subscribe(
        (assignments) => {
          this.assignments=assignments;
    },(error)=>{
      this.snackBar.open('Error loading assignments', 'Close', {
        duration: 5000,
        verticalPosition: 'top',
        horizontalPosition: 'right'
      });
    });
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
          this.snackBar.open('Error loading lectures', 'Close', {
            duration: 5000,
            verticalPosition: 'top',
            horizontalPosition: 'right'
          });
        }
      );
      this.stream.getVideos(this.courseId, instructorId).subscribe(
        (videos) => {
          this.videos = videos;
        },
        (error) => {
          this.snackBar.open('Error loading videos', 'Close', {
            duration: 5000,
            verticalPosition: 'top',
            horizontalPosition: 'right'
          });
        }
      );
    }
  }

  copyCoursePassword(): void {
    if (this.course?.courseCode) {
      this.clipboard.copy(this.course.password);
      console.log('Course Password copied to clipboard:', this.course.password);
      this.showCopyMessagePass();
    }
  }
  showCopyMessagePass(): void {
    this.snackBar.open('Course password copied to clipboard', 'Close', {
      duration: 2000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['snackbar-success']
    });
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
      },(error)=>{
        this.snackBar.open('Error loading Students enrolled in course', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
      }
    );
      
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
          console.log('Instructor details:', instructor);
          this.instructor = instructor;
        }, error => {
          this.snackBar.open('Error Fetching your details', 'Close', {
            duration: 5000,
            verticalPosition: 'top',
            horizontalPosition: 'right'
          });
        });
      }
    }
  }

  loadAnnouncements(): void {
    if (this.courseId) {
      console.log('Fetching announcements for course ID:', this.courseId);
      this.stream.getAnnouncementsByCourseId(this.courseId).subscribe(
        (data => {
          this.announcements = data.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
          console.log('Announcements retrieved:', this.announcements);
          this.announcements.forEach(announcement => {
            this.subscribeToCommentUpdates(announcement.id);
          });
        }),
        error => {
          this.snackBar.open('Error Fetching announcements', 'Close', {
            duration: 5000,
            verticalPosition: 'top',
            horizontalPosition: 'right'
          });
        }
      );
    }
  }
  loadQuizzes():void{
    const instructorId = localStorage.getItem('userID');
    if(this.courseId && instructorId){
      this.stream.getQuizzes(instructorId,this.courseId).subscribe(data=>{
        this.quizzes=data;

      })
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
        this.snackBar.open('Announcement Posted !', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
        this.announcements.push(result); 
        this.loadAnnouncements();
      }
      
    });
  }

  openAssignmentsDialog():void{
    const instructorId = localStorage.getItem('userID');
    const dialogRef = this.dialog.open(AssignmentsComponent, {
      width: '600px',
      data: { instructorId: instructorId, courseId: this.courseId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.snackBar.open('Assignment Posted !', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
        this.announcements.push(result); 
        this.loadAssignments();
      }
    });
  }
  openStudentDetails(): void {
    const dialogRef = this.dialog.open(StudentDetailsDialogComponent, {
      width: '500px',
      data: { students: this.students }
    });

    dialogRef.afterClosed().subscribe(() => {
      this.snackBar.open('Error fetching student details', 'Close', {
        duration: 5000,
        verticalPosition: 'top',
        horizontalPosition: 'right'
      });
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
    this.snackBar.open('Your Comment is UP!', 'Close', {
      duration: 5000,
      verticalPosition: 'top',
      horizontalPosition: 'right'
    });
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
        this.snackBar.open('Lecture/Video added successfully!', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
        this.loadLecturesAndVideos();
      }
    });
  }
  getFileName(filePath: string | undefined): string {
    if (!filePath) {
      return '';
    }
    return filePath.substring(filePath.lastIndexOf('/') + 1);
  }
  // updateAnnouncement(){
  //   const instructorId = localStorage.getItem('userID');
  //   if(this.courseId && instructorId)
  //   this.announcementService.updateAnnouncement(  
  //     this.courseId,
  //     instructorId,
  //     this.announcementId,
  //     this.title,
  //     this.content,
  //     this.materialType,
  //     this.fileToUpload
  //   ).subscribe(
  //     response => {
  //       console.log('Announcement updated successfully:', response);
  //     },
  //     error => {
  //       console.error('Error updating announcement:', error);
  //     }
  //   );
  // }
  openAddMaterialDialogLabs(): void {
    const instructorId = localStorage.getItem('userID');
    const dialogRef = this.dialog.open(LabsComponent, {
      width: '600px',
      data: { courseId: this.courseId, instructorId: instructorId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.snackBar.open('Lab added successfully !', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
        this.loadLabs();
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

  viewAssignSubmissions(assignmentId: number): void {
    this.router.navigate(['/assignment-submissions-instructor', assignmentId]);
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
    this.closePublishModal();
  }

  viewQuiz(quizId: string): void {
    this.router.navigate(['/mcq-quiz-viewer', quizId]);
  }
  viewQuizSubmissions(quizId: string): void {
    this.router.navigate(['/quiz-submissions', quizId]);
  }
  navigateToUngradedQuizzes(){
    this.router.navigate(['/ungraded-quizzes',this.courseId]);
  }
  downloadQuiz(quizId: number): void {
    this.stream.downloadQuiz(quizId).subscribe(
      (data: Blob) => {
        const blob = new Blob([data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        window.open(url); 
      },
      error => {
        console.error('Error downloading quiz:', error);
        this.snackBar.open('Error downloading quiz', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
      }
    );
  }


}