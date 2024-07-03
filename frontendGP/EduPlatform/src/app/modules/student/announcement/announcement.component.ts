// In announcement.component.ts
import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { AnnouncementService } from 'src/app/services/announcement/announcement.service';
import { WebSocketService } from 'src/app/services/websocket-service/websocket.service';
import { FileViewerDialogComponent } from 'src/app/file-viewer-dialog/file-viewer-dialog.component';
import { formatDate } from '@angular/common';
import { Observable, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';

@Component({
  selector: 'app-announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css']
})
export class AnnouncementComponent implements OnInit, OnDestroy {
  @Input() courseId: number | undefined;
  announcements: any[] = [];
  newComment: { [key: number]: string } = {};
  commentSubscriptions: Map<number, any> = new Map();
  userCache: Map<number, string> = new Map(); // Cache to hold userId and username pairs

  constructor(
    private announcementService: AnnouncementService,
    private webSocketService: WebSocketService,
    private route: ActivatedRoute,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseId = +params['id'];
      this.fetchAnnouncements();
    });

    const token = localStorage.getItem('authToken');
    if (token) {
      this.webSocketService.connect(token, () => {
        console.log('WebSocket connected.');
        this.subscribeToAnnouncementTopics();
      });
    }
  }

  ngOnDestroy(): void {
    this.commentSubscriptions.forEach(subscription => {
      if (subscription) {
        subscription.unsubscribe();
      }
    });
    this.webSocketService.disconnect();
  }

  fetchAnnouncements(): void {
    if (this.courseId) {
      this.announcementService.getAnnouncementsByCourseId(this.courseId).subscribe(data => {
        this.announcements = data;
        this.announcements.forEach(announcement => {
          this.fetchInitialComments(announcement.id);
          this.subscribeToCommentUpdates(announcement.id);
        });
      });
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

  fetchInitialComments(announcementId: number): void {
    this.announcementService.getCommentsByAnnouncementId(announcementId).subscribe(comments => {
      const announcement = this.announcements.find(a => a.id === announcementId);
      if (announcement) {
        announcement.comments = comments;
      }
    });
  }

  getUsername(userId: number): Observable<string> {
    if (this.userCache.has(userId)) {
      return of(this.userCache.get(userId) as string);
    } else {
      return this.announcementService.getUserDetails(localStorage.getItem("userID")!).pipe(
        switchMap(user => {
          this.userCache.set(userId, user.username);
          return of(user.username);
        }),
        catchError(() => of('Unknown User')) // Handle error and return a default value
      );
    }
  }

  subscribeToAnnouncementTopics(): void {
    this.announcements.forEach(announcement => {
      this.subscribeToCommentUpdates(announcement.id);
    });
  }

  subscribeToCommentUpdates(announcementId: number): void {
    const topic = `/topic/announcement/${announcementId}/comments`;
    const subscription = this.webSocketService.subscribeToComments(topic, (message: any) => {
      const announcement = this.announcements.find(a => a.id === announcementId);
      if (announcement) {
        announcement.comments.push(message);
      }
    });

    this.commentSubscriptions.set(announcementId, subscription);
  }

  formatDate(dateString: string): string {
    return formatDate(dateString, 'mediumDate', 'en-US');
  }

  toggleComments(announcementId: number): void {
    const announcement = this.announcements.find(a => a.id === announcementId);
    if (announcement) {
      announcement.showComments = !announcement.showComments;
    }
  }
}
