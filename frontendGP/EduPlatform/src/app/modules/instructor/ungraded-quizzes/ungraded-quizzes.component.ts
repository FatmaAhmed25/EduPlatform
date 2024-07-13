import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UngradedQuizDTO } from 'src/app/models/dtos/ungraded-quiz-dto';
import { AutoGradeService } from 'src/app/services/auto-grade-service/auto-grade.service';

@Component({
  selector: 'app-ungraded-quizzes',
  templateUrl: './ungraded-quizzes.component.html',
  styleUrls: ['./ungraded-quizzes.component.scss']
})
export class UngradedQuizzesComponent implements OnInit {
  quizzes: UngradedQuizDTO[] = [];
  courseId: any = null;

  constructor(private quizService: AutoGradeService,private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.courseId = +params.get('courseId')!; // Get courseId from route
      if (this.courseId) {
        this.fetchQuizzes();
      }
    });
    
  }

  fetchQuizzes(): void {
    this.quizService.getUngradedQuizzes(this.courseId).subscribe((data: UngradedQuizDTO[]) => {
      this.quizzes = data;
    });
  }

  navigateToMcqQuiz(quizId: number): void {
    window.location.href = `/mcq-quiz-viewer/${quizId}`;
  }

  navigateToAutoGrade(quizId: number): void {
    window.location.href = `/students-submissions/${quizId}`;
  }
}