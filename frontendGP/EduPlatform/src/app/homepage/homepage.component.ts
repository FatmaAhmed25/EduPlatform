import { Component, OnInit, Renderer2, ElementRef } from '@angular/core';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent {
  constructor(private renderer: Renderer2, private el: ElementRef) { }

  ngOnInit(): void {
    const menuBtn = this.el.nativeElement.querySelector('.menu-button');
    const navLinks = this.el.nativeElement.querySelector('.nav-links');
    this.renderer.listen(menuBtn, 'click', () => {
      this.renderer.addClass(navLinks, 'mobile-menu');
    });
  }

}
