import { Component, OnInit, Input } from '@angular/core';
import { AnnouncementService } from 'src/app/services/announcement/announcement.service';

@Component({
  selector: 'app-announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css']
})
export class AnnouncementComponent implements OnInit {
  @Input() announcementId: number | undefined;
  announcement: any;

  constructor(private announcementService: AnnouncementService) { }

  ngOnInit(): void {
    this.announcementService.getAnnouncement(4).subscribe(data => {
      this.announcement = data;
    });
  }
}
