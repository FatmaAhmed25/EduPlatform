import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizzesComponent } from 'src/app/modules/student/quizzes/quizzes.component';

describe('QuizzesComponent', () => {
  let component: QuizzesComponent;
  let fixture: ComponentFixture<QuizzesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QuizzesComponent]
    });
    fixture = TestBed.createComponent(QuizzesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
