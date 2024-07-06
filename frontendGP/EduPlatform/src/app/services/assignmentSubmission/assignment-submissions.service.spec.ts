import { TestBed } from '@angular/core/testing';

import { AssignmentSubmissionsService } from './assignment-submissions.service';

describe('AssignmentSubmissionsService', () => {
  let service: AssignmentSubmissionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AssignmentSubmissionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
