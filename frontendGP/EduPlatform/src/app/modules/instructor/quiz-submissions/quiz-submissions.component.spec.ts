import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizSubmissionsComponent } from './quiz-submissions.component';

describe('QuizSubmissionsComponent', () => {
  let component: QuizSubmissionsComponent;
  let fixture: ComponentFixture<QuizSubmissionsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QuizSubmissionsComponent]
    });
    fixture = TestBed.createComponent(QuizSubmissionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
