  import { Component, OnInit, Input } from '@angular/core';
  import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
  import { MatDialog } from '@angular/material/dialog';
  import { AssignmentService } from 'src/app/services/assignementService/assignement.service';
  import { SubmitDialogComponent } from 'src/app/modules/student/submit-dialog/submit-dialog.component';

  @Component({
    selector: 'app-assignment',
    templateUrl: './assignment.component.html',
    styleUrls: ['./assignment.component.css']
  })
  export class AssignmentComponent implements OnInit {
    @Input() courseId: number | undefined;
    @Input() studentId: string | undefined;
    assignments: any[] = [];
    selectedAssignmentId: number | null = null;
    fileUrl: SafeResourceUrl | null = null;
    
    constructor(
      private assignmentService: AssignmentService,
      private sanitizer: DomSanitizer,
      public dialog: MatDialog
    ) {}

    ngOnInit(): void {
      if (this.courseId) {
        this.fetchAssignments();
      }
    }

    fetchAssignments(): void {
      if (this.courseId) {
        this.assignmentService.getAssignments(this.courseId).subscribe(data => {
          this.assignments = data;
        });
      }
    }

    toggleAssignmentContent(assignment: any): void {
      if (this.selectedAssignmentId === assignment.id) {
        this.selectedAssignmentId = null;
      } else {
        this.selectedAssignmentId = assignment.id;
        this.openFileLink(assignment);
      }
    }

    openSubmitDialog(assignment: any): void {
      const studentId = localStorage.getItem('userID');
      if (studentId) {
        const dialogRef = this.dialog.open(SubmitDialogComponent, {
          width: '300px',
          data: { studentId: studentId, assignmentId: assignment.id }
        });

        dialogRef.afterClosed().subscribe(result => {
          console.log('The dialog was closed');
        });
      }
    }

    openFileLink(assignment: any): void {
      if (this.courseId && assignment.fileName) {
        this.assignmentService.getFileLink(this.courseId, assignment.fileName).subscribe((link: string) => {
          const cleanedLink = link.replace(/^"(.*)"$/, '$1');
          console.log('Cleaned Link:', cleanedLink); // Log the URL for debugging
          this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(cleanedLink);
        });
      }
    }

    openInNewTab(assignment: any): void {
      if (this.courseId && assignment.fileName) {
        this.assignmentService.getFileLink(this.courseId, assignment.fileName).subscribe((link: string) => {
          const cleanedLink = link.replace(/^"(.*)"$/, '$1');
          console.log('Cleaned Link:', cleanedLink); // Log the URL for debugging
          window.open(cleanedLink);
        });
      }
    }
    isPastDue(dueDate: string): boolean {
      const now = new Date();
      const due = new Date(dueDate);
      return now > due;
    }
  
  }
