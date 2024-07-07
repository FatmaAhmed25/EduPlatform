import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-instruction-modal',
  templateUrl: './instruction-modal.component.html',
  styleUrls: ['./instruction-modal.component.scss']
})
export class InstructionModalComponent {
  @Output() confirmed = new EventEmitter<void>();
  isVisible = false;

  show(): void {
    this.isVisible = true;
  }

  close(): void {
    this.isVisible = false;
  }

  confirm(): void {
    this.close();
    this.confirmed.emit();
  }
}
