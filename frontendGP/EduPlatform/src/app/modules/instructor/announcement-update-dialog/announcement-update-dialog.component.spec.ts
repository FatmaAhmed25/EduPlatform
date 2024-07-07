import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnnouncementUpdateDialogComponent } from './announcement-update-dialog.component';

describe('AnnouncementUpdateDialogComponent', () => {
  let component: AnnouncementUpdateDialogComponent;
  let fixture: ComponentFixture<AnnouncementUpdateDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AnnouncementUpdateDialogComponent]
    });
    fixture = TestBed.createComponent(AnnouncementUpdateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
