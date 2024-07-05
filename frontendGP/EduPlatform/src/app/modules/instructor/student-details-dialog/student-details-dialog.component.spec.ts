import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentDetailsDialogComponent } from './student-details-dialog.component';

describe('StudentDetailsDialogComponent', () => {
  let component: StudentDetailsDialogComponent;
  let fixture: ComponentFixture<StudentDetailsDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StudentDetailsDialogComponent]
    });
    fixture = TestBed.createComponent(StudentDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
