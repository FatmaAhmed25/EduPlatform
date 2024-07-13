import { Component, Renderer2, ElementRef ,AfterViewInit, ViewChild} from '@angular/core';
import { ViewportScroller } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { trigger, transition, style, query, stagger, animate } from '@angular/animations';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css'],
  animations: [
    trigger('pageAnimations', [
      transition(':enter', [
        query('nav, header, main', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(100, [
            animate('600ms cubic-bezier(0.35, 0, 0.25, 1)', style({ opacity: 1, transform: 'translateY(0)' })),
          ]),
        ]),
      ]),
    ]),
  ],
})
export class HomepageComponent {
  constructor(private renderer: Renderer2, private el: ElementRef,private viewportScroller:ViewportScroller,private route: ActivatedRoute) { }
  @ViewChild('featuresSection') featuresSectionRef!: ElementRef;
  ngOnInit(): void {
    const menuBtn = this.el.nativeElement.querySelector('.menu-button');
    const navLinks = this.el.nativeElement.querySelector('.nav-links');
    this.renderer.listen(menuBtn, 'click', () => {
      this.renderer.addClass(navLinks, 'mobile-menu');
    });
       this.route.fragment.subscribe(fragment => {
        if (fragment === 'features') {
          this.scrollTo('features');
        }
      });
  }
  scrollTo(anchor: string): void {
    const element = document.getElementById(anchor);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }
  ngAfterViewInit(): void {
    this.route.fragment.subscribe(fragment => {
      if (fragment === 'features' && this.featuresSectionRef.nativeElement) {
        this.featuresSectionRef.nativeElement.scrollIntoView({ behavior: 'smooth' });
      }
    });
}
}
