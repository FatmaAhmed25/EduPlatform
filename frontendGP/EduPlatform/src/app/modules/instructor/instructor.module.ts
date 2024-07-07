import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InstructorDashboardComponent } from './instructor-dashboard/instructor-dashboard.component';
import { CreateQuizComponent } from 'src/app/modules/instructor/create-quiz/create-quiz.component';
import { AiGenerateQuizComponent } from './ai-generate-quiz/ai-generate-quiz.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms'; 
import { CreateCourseComponent } from './create-course/create-course.component';
import { CoursesComponent } from './courses/courses.component';
import { SearchResultInstructorComponent } from './search-result-instructor/search-result-instructor.component';
import { StreamComponent } from './stream/stream.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { StudentDetailsDialogComponent } from './student-details-dialog/student-details-dialog.component';
import { MatListModule } from '@angular/material/list';
import { AnnouncementDialogComponent } from './announcement-dialog/announcement-dialog.component';
import { MatIconModule } from '@angular/material/icon';
import { LabsComponent } from './labs/labs.component';
import { LecturesComponent } from './lectures/lectures.component';
import { ManualMcqQuizComponent } from './manual-MCQ-quiz/manual-MCQ-quiz.component';
import { ManualEssayQuizComponent } from './manual-essay-quiz/manual-essay-quiz.component';
import { McqQuizViewerInstructorComponent } from './mcq-quiz-viewer-instructor/mcq-quiz-viewer-instructor.component';
import { QuizSubmissionsComponent } from './quiz-submissions/quiz-submissions.component';
import { QuizSubmissionsListComponent } from './quiz-submissions-list/quiz-submissions-list.component';
import { AssignmentsComponent } from './assignments/assignments.component';
import { AssignmentSubmissionsComponent } from './assignment-submissions/assignment-submissions.component';
import { UngradedQuizzesComponent } from './ungraded-quizzes/ungraded-quizzes.component';
<<<<<<< HEAD
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
=======
import { StudentsSubmissionsComponent } from './students-submissions/students-submissions/students-submissions.component';
import { UploadPdfComponent } from './upload-pdfs/upload-pdf/upload-pdf.component';
>>>>>>> 219a7927e101c478e320be6fc66cf8c8fcf64624

@NgModule({
  declarations: [
    InstructorDashboardComponent,
    CreateQuizComponent,
    AiGenerateQuizComponent,
    CreateCourseComponent,
    CoursesComponent,
    SearchResultInstructorComponent,
    StreamComponent,
    StudentDetailsDialogComponent,
    AnnouncementDialogComponent,
    LabsComponent,
    LecturesComponent,
    ManualMcqQuizComponent,
    ManualEssayQuizComponent,
    McqQuizViewerInstructorComponent,
    QuizSubmissionsComponent,
    QuizSubmissionsListComponent,
    AssignmentsComponent,
    AssignmentSubmissionsComponent,
    UngradedQuizzesComponent,
    StudentsSubmissionsComponent,
    UploadPdfComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatTabsModule,
    MatListModule,
    MatIconModule,
    BrowserAnimationsModule
  ],
  exports: [
    CreateQuizComponent,
  AiGenerateQuizComponent,
  InstructorDashboardComponent
]
})
export class InstructorModule { }
