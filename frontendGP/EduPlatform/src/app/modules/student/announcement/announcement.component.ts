import { Component, OnInit, Input } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AnnouncementService } from 'src/app/services/announcement/announcement.service';
import { ActivatedRoute } from '@angular/router';
import { FileViewerDialogComponent } from 'src/app/file-viewer-dialog/file-viewer-dialog.component';
import { formatDate } from '@angular/common';

@Component({
  selector: 'app-announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css']
})
export class AnnouncementComponent implements OnInit {
  @Input() courseId: number | undefined;
  announcements: any[] = [];

  constructor(
    private announcementService: AnnouncementService,
    private route: ActivatedRoute,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseId = +params['id'];
      this.fetchAnnouncements();
    });
  }

  fetchAnnouncements(): void {
    if (this.courseId) {
      this.announcementService.getAnnouncementsByCourseId(this.courseId).subscribe(data => {
        this.announcements = data;
      });
    }
  }

  openFileLink(announcement: any): void {
    if (this.courseId && announcement.fileName) {
      this.announcementService.getFileLink(this.courseId, announcement.fileName).subscribe(link => {
        const cleanedLink = link.replace(/^"(.*)"$/, '$1');

        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '80vw'; // 80% of the viewport width
        dialogConfig.height = '80vh'; // 80% of the viewport height
        dialogConfig.data = { fileUrl: cleanedLink, fileName: announcement.fileName };

        this.dialog.open(FileViewerDialogComponent, dialogConfig);
      });
    }
  }

  formatDate(dateString: string): string {
    return formatDate(dateString, 'mediumDate', 'en-US');
  }
}
