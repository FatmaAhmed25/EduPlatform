import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProctoredVideoComponent } from './proctored-video.component';

describe('ProctoredVideoComponent', () => {
  let component: ProctoredVideoComponent;
  let fixture: ComponentFixture<ProctoredVideoComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProctoredVideoComponent]
    });
    fixture = TestBed.createComponent(ProctoredVideoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
