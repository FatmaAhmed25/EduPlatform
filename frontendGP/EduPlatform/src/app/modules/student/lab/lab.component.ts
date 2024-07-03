// lab.component.ts
import { Component, OnInit, Input } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { LabService } from 'src/app/services/Lab/lab.service';

@Component({
  selector: 'app-lab',
  templateUrl: './lab.component.html',
  styleUrls: ['./lab.component.css']
})
export class LabComponent implements OnInit {
  @Input() courseId: number | undefined;
  labs: any[] = [];
  selectedLabId: number | null = null;
  fileUrl: SafeResourceUrl | null = null;

  constructor(
    private labService: LabService,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit(): void {
    if (this.courseId) {
      this.fetchLabs();
    }
  }

  fetchLabs(): void {
    if (this.courseId) {
      this.labService.getLabs(this.courseId).subscribe(data => {
        this.labs = data;
      });
    }
  }

  toggleLabContent(lab: any): void {
    if (this.selectedLabId === lab.id) {
      this.selectedLabId = null;
    } else {
      this.selectedLabId = lab.id;
      this.openFileLink(lab);
    }
  }

  openFileLink(lab: any): void {
    if (this.courseId && lab.fileName) {
      this.labService.getFileLink(this.courseId, lab.fileName).subscribe((link: string) => {
        const cleanedLink = link.replace(/^"(.*)"$/, '$1');
        console.log('Cleaned Link:', cleanedLink); // Log the URL for debugging
        this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(cleanedLink);
      });
    }
  }

  openInNewTab(lab: any): void {
    if (this.courseId && lab.fileName) {
      this.labService.getFileLink(this.courseId, lab.fileName).subscribe((link: string) => {
        const cleanedLink = link.replace(/^"(.*)"$/, '$1');
        console.log('Cleaned Link:', cleanedLink); // Log the URL for debugging
        window.open(cleanedLink);
      });
    }
  }
}
