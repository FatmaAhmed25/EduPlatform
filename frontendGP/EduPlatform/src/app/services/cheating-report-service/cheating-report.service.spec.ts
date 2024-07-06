import { TestBed } from '@angular/core/testing';

import { CheatingReportService } from './cheating-report.service';

describe('CheatingReportService', () => {
  let service: CheatingReportService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CheatingReportService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
