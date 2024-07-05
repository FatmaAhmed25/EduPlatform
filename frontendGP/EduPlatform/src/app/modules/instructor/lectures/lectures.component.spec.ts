import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LecturesComponent } from './lectures.component';

describe('LecturesComponent', () => {
  let component: LecturesComponent;
  let fixture: ComponentFixture<LecturesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LecturesComponent]
    });
    fixture = TestBed.createComponent(LecturesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
