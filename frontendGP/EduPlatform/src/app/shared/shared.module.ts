import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProctoredVideoComponent } from '../proctored-video/proctored-video.component';

@NgModule({
  declarations: [
    ProctoredVideoComponent
  ],
  imports: [
    CommonModule,
    MatProgressSpinnerModule,
  ],
  exports: [
    MatProgressSpinnerModule,
    ProctoredVideoComponent
  ]
})
export class SharedModule { }
