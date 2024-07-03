import { Component, OnInit, Input } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { FileViewerDialogComponent } from 'src/app/file-viewer-dialog/file-viewer-dialog.component';
import { LectureService } from 'src/app/services/lectureService/lecture.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-lecture',
  templateUrl: './lecture.component.html',
  styleUrls: ['./lecture.component.css']
})
export class LectureComponent implements OnInit {
  @Input() courseId: number | undefined;
  @Input() studentId: number | undefined;
  lectures: any[] = [];
  selectedLectureId: number | null = null;
  fileUrl: SafeResourceUrl | null = null;

  constructor(
    private lectureService: LectureService,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit(): void {
    if (this.courseId && this.studentId) {
      this.fetchLectures();
    }
  }

  fetchLectures(): void {
    if (this.courseId && this.studentId) {
      this.lectureService.getLectures(this.studentId, this.courseId).subscribe(data => {
        this.lectures = data;
      });
    }
  }

  toggleLectureContent(lecture: any): void {
    if (this.selectedLectureId === lecture.id) {
      this.selectedLectureId = null;
    } else {
      this.selectedLectureId = lecture.id;
      this.openFileLink(lecture);
    }
  }

  openFileLink(lecture: any): void {
    if (this.courseId && lecture.fileName) {
      this.lectureService.getFileLink(this.courseId, lecture.fileName).subscribe((link: string) => {
        const cleanedLink = link.replace(/^"(.*)"$/, '$1');
        console.log('Cleaned Link:', cleanedLink); // Log the URL for debugging
        this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(cleanedLink);
      });
    }
  }
  openInNewTab(lecture: any ): void {
    if (this.courseId && lecture.fileName) {
      this.lectureService.getFileLink(this.courseId, lecture.fileName).subscribe((link: string) => {
        const cleanedLink = link.replace(/^"(.*)"$/, '$1');
        console.log('Cleaned Link:', cleanedLink); // Log the URL for debugging
        window.open(cleanedLink);
      });
    }
  }
}