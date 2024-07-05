import { TestBed } from '@angular/core/testing';

import { ManualQuizService } from './manual-quiz.service';

describe('ManualQuizService', () => {
  let service: ManualQuizService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManualQuizService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
