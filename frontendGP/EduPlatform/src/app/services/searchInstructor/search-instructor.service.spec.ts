import { TestBed } from '@angular/core/testing';

import { SearchInstructorService } from './search-instructor.service';

describe('SearchInstructorService', () => {
  let service: SearchInstructorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SearchInstructorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
