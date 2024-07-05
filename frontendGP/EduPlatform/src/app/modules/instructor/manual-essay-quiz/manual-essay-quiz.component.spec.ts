import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManualEssayQuizComponent } from './manual-essay-quiz.component';

describe('ManualESSAYQuizComponent', () => {
  let component: ManualEssayQuizComponent;
  let fixture: ComponentFixture<ManualEssayQuizComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ManualEssayQuizComponent]
    });
    fixture = TestBed.createComponent(ManualEssayQuizComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
