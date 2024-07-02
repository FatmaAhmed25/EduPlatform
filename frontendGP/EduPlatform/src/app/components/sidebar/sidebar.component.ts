import { Component, OnInit, Renderer2 } from '@angular/core';
import { SidebarService } from 'src/app/services/sidebar-Service/sidebar.service';
@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  constructor(private renderer: Renderer2, private sidebarService: SidebarService) {}

  ngOnInit(): void {
    const body = document.body;
    const sidebar = document.querySelector('nav') as HTMLElement;
    const toggle = document.querySelector(".toggle") as HTMLElement;
    const searchBtn = document.querySelector(".search-box") as HTMLElement;
    const modeSwitch = document.querySelector(".toggle-switch") as HTMLElement;
    const modeText = document.querySelector(".mode-text") as HTMLElement;

    if (toggle) {
      toggle.addEventListener("click", () => {
        sidebar.classList.toggle("close");
        body.classList.toggle("sidebar-open");
      });
    }
    
    if (searchBtn) {
      searchBtn.addEventListener("click", () => {
        sidebar.classList.remove("close");
        body.classList.add("sidebar-open");
      });
    }

    if (modeSwitch) {
      modeSwitch.addEventListener("click", () => {
        body.classList.toggle("dark");
        // Update mode text
        if (body.classList.contains("dark")) {
          modeText.innerText = "Light mode";
        } else {
          modeText.innerText = "Dark mode";
        }
      });
    }
  }
  toggleSidebar() {
    this.sidebarService.toggleSidebar();
  }
}
