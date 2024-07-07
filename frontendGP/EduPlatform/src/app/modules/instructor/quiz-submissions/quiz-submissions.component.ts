import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { CourseDTO } from 'src/app/models/dtos/coursedto';
import { instructorQuizSubmissionDTO } from 'src/app/models/dtos/instructor-quiz-submission-dto';
import { CheatingReportService } from 'src/app/services/cheating-report-service/cheating-report.service';
import { QuizSubmissionsService } from 'src/app/services/quiz-submission-servie/quiz-submissions.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-quiz-submissions',
  templateUrl: './quiz-submissions.component.html',
  styleUrls: ['./quiz-submissions.component.scss']
})
export class QuizSubmissionsComponent implements OnInit {

  submissions: instructorQuizSubmissionDTO[] = [];
  // submissions: any[] = [];
  quizId!: number;
  instructorId: string = localStorage.getItem("userID")!; // Replace with actual instructor ID
  constructor(private snackBar:MatSnackBar, private cheatingReportService: CheatingReportService,private quizSubmissionsService: QuizSubmissionsService,private route: ActivatedRoute ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.quizId = params['quizId'];
      this.fetchSubmittedQuizzes();
    });
  }
  downloadCheatingReport(submissionId: number): void {
    this.cheatingReportService.downloadReport(submissionId).subscribe(
      (data: Blob) => {
        const blob = new Blob([data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        window.open(url); 
      },
      error => {
        console.error('Error downloading cheating report:', error);
        console.error('Error downloading cheating report:', error);
          this.snackBar.open('Error downloading cheating report', 'Close', {
            duration: 5000,
            verticalPosition: 'top',
            horizontalPosition: 'right'
          });
        
      }
    );
  }
  downloadQuizSubmissionPdf(submissionId: number, quizId: number): void {
    const studentId = Number(localStorage.getItem('userID'));
    
    if (!isNaN(studentId) && submissionId && quizId) {
      this.quizSubmissionsService.downloadSubmissionPdf(studentId, quizId).subscribe(
        (data: Blob) => {
          const blob = new Blob([data], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `Quiz_Submission_${submissionId}.pdf`; // Set the filename for download
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
        },
        error => {
          console.error('Error downloading quiz submission PDF:', error);
          this.snackBar.open('Error downloading quiz submission PDF', 'Close', {
            duration: 5000,
            verticalPosition: 'top',
            horizontalPosition: 'right'
          });
        }
      );
    } else {
      console.error('Invalid student ID, submission ID, or quiz ID.');
      this.snackBar.open('Invalid student ID, submission ID, or quiz ID', 'Close', {
        duration: 5000,
        verticalPosition: 'top',
        horizontalPosition: 'right'
      });
    }
  }
  

  fetchSubmittedQuizzes(): void {
    this.quizSubmissionsService.getQuizSubmissionsForInstructor(this.instructorId, this.quizId).subscribe(data => {
      this.submissions = data;
        console.log('Submitted Quizzes:', this.submissions);
      },
      error => {
        console.error('Error fetching submitted quizzes:', error);
          this.snackBar.open('Error fetching submitted quizzes', 'Close', {
            duration: 5000,
            verticalPosition: 'top',
            horizontalPosition: 'right'
          });

      }
    );
  }

  togglePasswordVisibility(course: any) {
    course.showPassword = !course.showPassword; // Toggle password visibility flag
}
  getUserIdFromLocalStorage(): any | null {
    const userId = localStorage.getItem('userID');
    return userId ? +userId : null; // Convert to number or return null if not found
  }





}
