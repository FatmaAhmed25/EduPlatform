import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-student-details-dialog',
  templateUrl: './student-details-dialog.component.html',
  styleUrls: ['./student-details-dialog.component.scss']
})
export class StudentDetailsDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<StudentDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { students: any[] }
  ) {}

  close(): void {
    this.dialogRef.close();
  }
}
