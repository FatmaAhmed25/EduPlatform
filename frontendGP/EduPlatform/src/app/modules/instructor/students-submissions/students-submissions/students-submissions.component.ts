import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { StudentSubmissionDTO } from 'src/app/models/dtos/student-submission-dto';
import { AutoGradeService } from 'src/app/services/auto-grade-service/auto-grade.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { UploadPdfComponent } from '../../upload-pdfs/upload-pdf/upload-pdf.component';

@Component({
  selector: 'app-students-submissions',
  templateUrl: './students-submissions.component.html',
  styleUrls: ['./students-submissions.component.scss']
})
export class StudentsSubmissionsComponent implements OnInit {
  studentSubmissions: StudentSubmissionDTO[] = [];
  loading = false;
  quizId: number | null = null;

  constructor(
    private snackBar: MatSnackBar,
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private autoGradeService: AutoGradeService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.quizId = +params.get('quizId')!;
      if (this.quizId) {
        this.loadStudentSubmissions();
      }
    });
  }

  loadStudentSubmissions(): void {
    this.autoGradeService.getStudentSubmissions(this.quizId!).subscribe((data: StudentSubmissionDTO[]) => {
      this.studentSubmissions = data;
    });
  }

  openUploadPdfDialog(): void {
    const dialogRef = this.dialog.open(UploadPdfComponent, {
      width: '700px', height: '500px'
    });
  }

  autoGrade(quizId: number, studentId: number): void {
    // Open the upload dialog
    const dialogRef = this.dialog.open(UploadPdfComponent, {
      width: '600px',
    });

    // Listen for when the dialog closes
    dialogRef.afterClosed().subscribe(result => {
      if (this.autoGradeService.files.length > 0) {  // Check if files are set
        this.loading = true;
        this.autoGradeService.autoGradeQuiz(quizId, studentId).subscribe(response => {
          this.loading = false;
          console.log('Auto grade response:', response);
          this.snackBar.open('Auto grading completed successfully!', 'Close', {
            duration: 5000,
            verticalPosition: 'top',
            horizontalPosition: 'right'
          });
        }, error => {
          this.loading = false;
          console.error('Error auto grading:', error);
          this.snackBar.open('Error during auto grading.', 'Close', {
            duration: 5000,
            verticalPosition: 'top',
            horizontalPosition: 'right'
          });
        });
      } else {
        // Show error message if no files were selected
        this.snackBar.open('Please select at least one PDF file.', 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
      }
    });
  }
}
