import { Component,Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AnnouncementService } from 'src/app/services/announcementDialog/announcement.service';
@Component({
  selector: 'app-lectures',
  templateUrl: './lectures.component.html',
  styleUrls: ['./lectures.component.scss']
})
export class LecturesComponent {
  lecture = {
    title: '',
    type: 'LECTURE',
    content: ''
  };
  file: File | null = null;
  constructor(
    public dialogRef: MatDialogRef<LecturesComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private announcementService: AnnouncementService
  ) {}
  onClose(): void {
    this.dialogRef.close();
  }


  onFileChange(event: any): void {
    this.file = event.target.files[0];
  }
  onSubmit(): void {
    const materialType = this.lecture.type === 'LECTURE' ? 'LECTURES' : 'VIDEOS';
    if (this.file) {
      this.announcementService.uploadAnnouncementWithFile(
        this.data.courseId,
        this.data.instructorId,
        this.lecture.title,
        this.lecture.content,
        this.file,
        materialType
      ).subscribe(response => {
        console.log('Lecture with file uploaded:', response);
        this.dialogRef.close(response);
      }, error => {
        console.error('Error uploading lecture with file:', error);
      });
    } 
  }
}
