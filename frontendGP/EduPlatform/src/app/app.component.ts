import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'EduPlatform';
  showSidebar: boolean = true;
  showNavbar: boolean = true;

  constructor(private router: Router) {}

  ngOnInit() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.showSidebar = !(event.url === '/' || event.url === '/login'|| event.url === '/take-quiz' ||event.url === '/#features');
        this.showNavbar = !(event.url === '/' || event.url === '/login'|| event.url === '/take-quiz' ||event.url === '/#features');
      }
    });
  }
}
