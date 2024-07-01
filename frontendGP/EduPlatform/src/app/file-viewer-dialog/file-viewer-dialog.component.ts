import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-file-viewer-dialog',
  templateUrl: './file-viewer-dialog.component.html',
  styleUrls: ['./file-viewer-dialog.component.css']
})
export class FileViewerDialogComponent implements OnInit {
  fileUrl: string;
  fileName: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
    this.fileUrl = data.fileUrl;
    this.fileName = data.fileName;
  }

  ngOnInit(): void {}
  downloadFile(): void {
    const a = document.createElement('a');
    a.href = this.fileUrl;
    a.download = this.fileName;
    a.target = '_blank';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }
  
}
