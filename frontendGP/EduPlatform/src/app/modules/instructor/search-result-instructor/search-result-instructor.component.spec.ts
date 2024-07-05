import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchResultInstructorComponent } from './search-result-instructor.component';

describe('SearchResultInstructorComponent', () => {
  let component: SearchResultInstructorComponent;
  let fixture: ComponentFixture<SearchResultInstructorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SearchResultInstructorComponent]
    });
    fixture = TestBed.createComponent(SearchResultInstructorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
