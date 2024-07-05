import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManualMcqQuizComponent } from './manual-MCQ-quiz.component';

describe('ManualQuizComponent', () => {
  let component: ManualMcqQuizComponent;
  let fixture: ComponentFixture<ManualMcqQuizComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ManualMcqQuizComponent]
    });
    fixture = TestBed.createComponent(ManualMcqQuizComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
