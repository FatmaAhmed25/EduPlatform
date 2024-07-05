import { TestBed } from '@angular/core/testing';

import { ProctoredVideoService } from './proctored-video.service';

describe('ProctoredVideoService', () => {
  let service: ProctoredVideoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProctoredVideoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
