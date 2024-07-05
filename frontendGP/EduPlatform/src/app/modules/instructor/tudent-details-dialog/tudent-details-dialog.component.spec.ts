import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TudentDetailsDialogComponent } from './tudent-details-dialog.component';

describe('TudentDetailsDialogComponent', () => {
  let component: TudentDetailsDialogComponent;
  let fixture: ComponentFixture<TudentDetailsDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TudentDetailsDialogComponent]
    });
    fixture = TestBed.createComponent(TudentDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
