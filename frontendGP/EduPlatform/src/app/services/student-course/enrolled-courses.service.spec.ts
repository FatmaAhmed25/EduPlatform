import { TestBed } from '@angular/core/testing';

import { EnrolledCoursesService } from './enrolled-courses.service';

describe('EnrolledCoursesService', () => {
  let service: EnrolledCoursesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnrolledCoursesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
