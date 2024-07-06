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

        const isTakeQuizRoute = /\/take-quiz\/\d+/.test(event.url);
        const isRootOrLogin = event.url === '/' || event.url === '/login';
        const isFeatureSection = event.url === '/#features';

        this.showSidebar = !(isRootOrLogin || isTakeQuizRoute || isFeatureSection);
        this.showNavbar = !(isRootOrLogin || isTakeQuizRoute || isFeatureSection);
      }
    });
  }
}
