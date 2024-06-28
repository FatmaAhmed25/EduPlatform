import { Component } from '@angular/core';

@Component({
  selector: 'app-instructor-dashboard',
  templateUrl: './instructor-dashboard.component.html',
  styleUrls: ['./instructor-dashboard.component.scss']
})
export class InstructorDashboardComponent {
  token: string | null = '';

  ngOnInit() {
    this.token = localStorage.getItem('authToken');
  }
}