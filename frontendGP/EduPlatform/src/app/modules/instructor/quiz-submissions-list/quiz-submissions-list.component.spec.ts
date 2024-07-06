import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizSubmissionsListComponent } from './quiz-submissions-list.component';

describe('QuizSubmissionsListComponent', () => {
  let component: QuizSubmissionsListComponent;
  let fixture: ComponentFixture<QuizSubmissionsListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QuizSubmissionsListComponent]
    });
    fixture = TestBed.createComponent(QuizSubmissionsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
