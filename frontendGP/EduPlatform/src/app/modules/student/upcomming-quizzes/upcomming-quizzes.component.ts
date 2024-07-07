import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { UpcomingQuizDTO } from 'src/app/models/dtos/UpcomingQuizDTO';
import { UpcomingQuizzesService } from 'src/app/services/upcoming-quizzes/upcoming-quizzes.service';
import { InstructionModalComponent } from '../../../components/instruction-modal/instruction-modal.component';

@Component({
  selector: 'app-upcomming-quizzes',
  templateUrl: './upcomming-quizzes.component.html',
  styleUrls: ['./upcomming-quizzes.component.scss']
})
export class UpcommingQuizzesComponent implements OnInit {
  quizzes: UpcomingQuizDTO[] = [];
  selectedQuiz: any;

  @ViewChild('instructionModal') instructionModal!: InstructionModalComponent;

  constructor(private upcomingQuizzesService: UpcomingQuizzesService, private router: Router) {}

  ngOnInit(): void {
    this.fetchUpcomingQuizzes();
  }

  fetchUpcomingQuizzes(): void {
    const studentId = this.getUserIdFromLocalStorage();
    if (studentId) {
      this.upcomingQuizzesService.getUpcomingQuizzes(studentId).subscribe(
        quizzes => {
          this.quizzes = quizzes;
          console.log('Upcoming Quizzes:', this.quizzes);
        },
        error => {
          console.error('Error fetching upcoming quizzes:', error);
        }
      );
    }
  }

  getUserIdFromLocalStorage(): number | null {
    const userId = localStorage.getItem('userID');
    return userId ? +userId : null;
  }

  takeQuiz(courseId: any, quizId: number): void {
    this.selectedQuiz = { courseId, quizId };
    this.instructionModal.show();
  }

  onModalConfirmed(): void {
    const { courseId, quizId } = this.selectedQuiz;
    this.router.navigate(['/take-quiz', courseId, quizId]);
  }

  displayTime(date: any): string {
    const time = new Date(date);
    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1); 

    if (time.toDateString() === today.toDateString()) {
        // Today's date logic
        const hours = time.getHours();
        const minutes = time.getMinutes();
        const period = hours >= 12 ? 'PM' : 'AM';
        const displayHours = hours % 12 || 12; // Convert to 12-hour format

        return `Today ${displayHours}:${minutes < 10 ? '0' + minutes : minutes} ${period}`;
    } else if (time.toDateString() === tomorrow.toDateString()) {
        // Tomorrow's date logic
        const hours = time.getHours();
        const minutes = time.getMinutes();
        const period = hours >= 12 ? 'PM' : 'AM';
        const displayHours = hours % 12 || 12; // Convert to 12-hour format

        return `Tomorrow ${displayHours}:${minutes < 10 ? '0' + minutes : minutes} ${period}`;
    } else {
        // Other dates logic
        const formattedDate = time.toLocaleDateString();
        const formattedTime = time.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

        return `${formattedDate} ${formattedTime}`;
    }
  }

  countdownText(startTime: any): string {
    const startDateTime = new Date(startTime);
    const now = new Date();

    if (now >= startDateTime) {
      return 'Available';
    }

    const timeDiff = startDateTime.getTime() - now.getTime();
    const daysDiff = Math.floor(timeDiff / (1000 * 60 * 60 * 24));
    const hoursDiff = Math.floor((timeDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutesDiff = Math.floor((timeDiff % (1000 * 60 * 60)) / (1000 * 60));

    if (daysDiff > 0) {
      return `${daysDiff} day${daysDiff > 1 ? 's' : ''}`;
    } else if (hoursDiff > 0) {
      return `${hoursDiff} hour${hoursDiff > 1 ? 's' : ''}`;
    } else {
      return `${minutesDiff} minute${minutesDiff !== 1 ? 's' : ''}`;
    }
  }

  getDuration(startTime: any, endTime: any): string {
    const start = new Date(startTime);
    const end = new Date(endTime);
    const durationInMilliseconds = end.getTime() - start.getTime();

    const days = Math.floor(durationInMilliseconds / (1000 * 60 * 60 * 24));
    const hours = Math.floor((durationInMilliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((durationInMilliseconds % (1000 * 60 * 60)) / (1000 * 60));

    if (days > 0) {
      return `${days} day${days > 1 ? 's' : ''}`;
    } else if (hours > 0) {
      return `${hours} hour${hours > 1 ? 's' : ''}`;
    } else {
      return `${minutes} minute${minutes !== 1 ? 's' : ''}`;
    }
  }
}
