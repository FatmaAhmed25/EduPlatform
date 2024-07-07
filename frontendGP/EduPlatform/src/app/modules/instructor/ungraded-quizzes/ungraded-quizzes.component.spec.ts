import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UngradedQuizzesComponent } from './ungraded-quizzes.component';

describe('UngradedQuizzesComponent', () => {
  let component: UngradedQuizzesComponent;
  let fixture: ComponentFixture<UngradedQuizzesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UngradedQuizzesComponent]
    });
    fixture = TestBed.createComponent(UngradedQuizzesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
