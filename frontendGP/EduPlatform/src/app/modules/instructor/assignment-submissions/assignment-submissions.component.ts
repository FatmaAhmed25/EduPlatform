import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AssignmentSubmissionsService } from 'src/app/services/assignmentSubmission/assignment-submissions.service';
import { AnnouncementService } from 'src/app/services/announcementDialog/announcement.service';
import { Student } from 'src/app/models/Student.model';
import { Instructor } from 'src/app/models/instructor.model';
import { Courses } from 'src/app/models/course.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Clipboard } from '@angular/cdk/clipboard';
import { FileSubmission } from 'src/app/models/fileSubmission.model';
import { FileViewerDialogComponent } from 'src/app/file-viewer-dialog/file-viewer-dialog.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';

@Component({
  selector: 'app-assignment-submissions',
  templateUrl: './assignment-submissions.component.html',
  styleUrls: ['./assignment-submissions.component.scss']
})
export class AssignmentSubmissionsComponent implements OnInit {
  assignmentId: number | null = null; 
  submissions: any[] = [];
  link2:string='';
  students: Student[] = [];
  instructor: Instructor | undefined; 
  course: Courses | undefined; 
  courseId:number | null=null;
  constructor(
    private route: ActivatedRoute,
    private assignmentService: AssignmentSubmissionsService,
    private stream:AnnouncementService,
    private clipboard: Clipboard,
    private snackBar: MatSnackBar,
    public dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    const courseId = this.route.snapshot.paramMap.get('CourseId');
    this.assignmentId = id ? +id : null;
    this.courseId=courseId ? +courseId : null;
    if (this.assignmentId !== null) {
      this.loadSubmissions();
    }
  }
  openFile(announcement: any): void {
    if (this.courseId && announcement.fileName) {
      this.stream.getFileLink(this.courseId, announcement.fileName).subscribe(link => {
        const cleanedLink = link.replace(/^"(.*)"$/, '$1');
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '80vw';
        dialogConfig.height = '80vh';
        dialogConfig.data = { fileUrl: cleanedLink, fileName: announcement.fileName };
        this.dialog.open(FileViewerDialogComponent, dialogConfig);
      });
    }
  }

copyCourseCode(): void {
  if (this.course?.courseCode) {
    this.clipboard.copy(this.course.courseCode);
    console.log('Course code copied to clipboard:', this.course.courseCode);
    this.showCopyMessage();
  }
}
  showCopyMessage(): void {
    this.snackBar.open('Course code copied to clipboard', 'Close', {
      duration: 2000,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      panelClass: ['snackbar-success']
    });
  }
  getFileName(filePath: string | undefined): string {
    if (!filePath) {
      return '';
    }
    return filePath.substring(filePath.lastIndexOf('/') + 1);
  }
  
  loadSubmissions(): void {
    this.assignmentService.getSubmissions(this.assignmentId!).subscribe(
      (data: FileSubmission[]) => {
        this.submissions = data;
      },
      (error) => {
        console.error('Error fetching submissions:', error);
      }
    );
  }
  
}
