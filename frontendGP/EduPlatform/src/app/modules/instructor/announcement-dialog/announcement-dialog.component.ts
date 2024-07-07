import { Component,Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AnnouncementService } from 'src/app/services/announcementDialog/announcement.service';

@Component({
  selector: 'app-announcement-dialog',
  templateUrl: './announcement-dialog.component.html',
  styleUrls: ['./announcement-dialog.component.scss']
})
export class AnnouncementDialogComponent {
  announcement = { title: '', content: '' };
  file: File | null = null;

  constructor(
    public dialogRef: MatDialogRef<AnnouncementDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,    private announcementService:AnnouncementService
  ) {
  }

  onFileChange(event: any) {
    this.file = event.target.files[0];
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    const materialType = 'OTHERS';
    if (this.file) {
      // File is present, use the upload material API
      this.announcementService.uploadAnnouncementWithFile(
        this.data.courseId,
        this.data.instructorId,
        this.announcement.title,
        this.announcement.content,
        this.file,
        materialType
      ).subscribe(response => {
        console.log('Announcement with file uploaded:', response);
        this.dialogRef.close(response);
      }, error => {
        console.error('Error uploading announcement with file:', error);
      });
    } else {
      this.announcementService.createAnnouncement(
        this.data.courseId,
        this.data.instructorId,
        this.announcement.title,
        this.announcement.content
      ).subscribe(response => {
        console.log('Announcement created:', response);
        this.dialogRef.close(response);
      }, error => {
        console.error('Error creating announcement:', error);
      });
    }
  }

}
