import { Component,Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { StreamService } from 'src/app/services/stream/stream.service';
@Component({
  selector: 'app-assignments',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.scss']
})
export class AssignmentsComponent {
  Assignment = {
    title: '',
    type: 'ASSIGNMENT',
    content: '',
    allowLateSubmission:'true',
    dueDate: '',
  };
  file: File | null = null;
  constructor(
    public dialogRef: MatDialogRef<AssignmentsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private stream: StreamService
  ) {}
  onClose(): void {
    this.dialogRef.close();
  }
  onFileChange(event: any): void {
    this.file = event.target.files[0];
  }
  onSubmit(): void {
    if (this.file) {
      const allowLateSubmission = this.Assignment.allowLateSubmission === 'true';
      const dueDate = new Date(this.Assignment.dueDate);

      this.stream.createAssignment(
        this.data.courseId,
        this.data.instructorId,
        dueDate,
        this.Assignment.title,
        this.Assignment.content,
        allowLateSubmission,
        this.file
      ).subscribe(
        response => {
          this.dialogRef.close(response);
        },
        error => {
          console.error('Error uploading file', error);
        }
      );
    } else {
      console.error('No file selected');
    }
  }
}
