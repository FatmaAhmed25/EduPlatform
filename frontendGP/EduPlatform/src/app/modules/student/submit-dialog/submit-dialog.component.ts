import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AssignmentService } from 'src/app/services/assignementService/assignement.service';

@Component({
  selector: 'app-submit-dialog',
  templateUrl: './submit-dialog.component.html',
  styleUrls: ['./submit-dialog.component.css']
})
export class SubmitDialogComponent {
  selectedFile: File | null = null;
  loading: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<SubmitDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { studentId: string, assignmentId: number },
    private assignmentService: AssignmentService
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  submitAssignment(): void {
    if (this.selectedFile) {
      this.loading = true;
      this.assignmentService.submitAssignment(this.data.studentId, this.data.assignmentId, this.selectedFile).subscribe(response => {
        console.log('Response:', response);  // Log the response
        this.loading = false;
        alert('Assignment submitted successfully');
        this.dialogRef.close();
      }, error => {
        console.log('Error:', error);  // Log the error
        this.loading = false;
        console.error('Error submitting assignment', error);
        alert('Failed to submit assignment');
      });
    }
  }
}
