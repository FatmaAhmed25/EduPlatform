import { Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AutoGradeService } from 'src/app/services/auto-grade-service/auto-grade.service';

@Component({
  selector: 'app-upload-pdf',
  templateUrl: './upload-pdf.component.html',
  styleUrls: ['./upload-pdf.component.scss']
})
export class UploadPdfComponent {
  validationErrors: string[] = [];
  selectedFiles: File[] = [];

  constructor(private autoGradeService: AutoGradeService, private snackBar: MatSnackBar) {}

  onFilesSelected(event: any): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const newFiles = Array.from(input.files);
      const allowedType = 'application/pdf';
      const allowedExtension = '.pdf';
      const invalidFiles: string[] = [];

      const filteredFiles = newFiles.filter(file => {
        const isPDF = file.type === allowedType && file.name.toLowerCase().endsWith(allowedExtension);
        if (!isPDF) {
          invalidFiles.push(file.name);
        }
        return isPDF;
      });

      this.selectedFiles = [...this.selectedFiles, ...filteredFiles];
      this.selectedFiles = Array.from(new Set(this.selectedFiles.map(file => file.name)))
                               .map(name => this.selectedFiles.find(file => file.name === name) as File);

      if (invalidFiles.length > 0) {
        alert(`The following files are not allowed and were ignored: ${invalidFiles.join(', ')}. Please upload PDF files only.`);
      }

      // Set files in the service
      this.autoGradeService.setFiles(this.selectedFiles);
    }
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.autoGradeService.setFiles(this.selectedFiles);
  }

  saveFiles(): void {
    if (this.selectedFiles.length > 0) {
      this.autoGradeService.setFiles(this.selectedFiles);
      this.snackBar.open('Files saved successfully!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right'
      });
    } else {
      this.snackBar.open('No files selected. Please upload at least one PDF file.', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
        panelClass: ['error-snackbar']
      });
    }
  }
}
