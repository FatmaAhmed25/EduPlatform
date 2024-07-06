import { Component ,Inject} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AnnouncementService } from 'src/app/services/announcementDialog/announcement.service';
@Component({
  selector: 'app-labs',
  templateUrl: './labs.component.html',
  styleUrls: ['./labs.component.scss']
})
export class LabsComponent {
  lab = {
    title: '',
    type: 'LAB',
    content: ''
  };
  file: File | null = null;
  constructor(
    public dialogRef: MatDialogRef<LabsComponent>,
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
    this.lab.type='LABS';
    const materialType = this.lab.type;
    if (this.file) {
      this.announcementService.uploadAnnouncementWithFile(
        this.data.courseId,
        this.data.instructorId,
        this.lab.title,
        this.lab.content,
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
