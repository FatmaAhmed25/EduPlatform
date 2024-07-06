import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { io, Socket } from 'socket.io-client';
import * as faceapi from '@vladmandic/face-api';
import * as cocoSsd from '@tensorflow-models/coco-ssd';
import * as dayjs from 'dayjs';
import { ProctoredVideoService } from '../services/proctored-service/proctored-video.service';

@Component({
  selector: 'app-proctored-video',
  templateUrl: './proctored-video.component.html',
  styleUrls: ['./proctored-video.component.scss']
})
export class ProctoredVideoComponent implements OnInit, OnDestroy {
  @ViewChild('videoEl') videoEl!: ElementRef<HTMLVideoElement>;
  @ViewChild('canvasEl') canvasEl!: ElementRef<HTMLCanvasElement>;
  
  displayText: string = '';
  private socket!: Socket;
  private detectionInterval: any;
  private lastFrameTime: number = 0;
  isVideoVisible: boolean = false;
  mediaStream: MediaStream | null = null;

  constructor(private proctoredVideoService: ProctoredVideoService) {}

  ngOnInit(): void {
    this.startDetection();
    this.handleSocket();
    document.addEventListener('visibilitychange', this.handleVisibilityChange);
  }

  ngOnDestroy(): void {
    this.stopFaceDetection();
    if (this.socket && this.socket.connected) {
      this.socket.disconnect();
    }
    document.removeEventListener('visibilitychange', this.handleVisibilityChange);
  }
  toggleVideoVisibility(): void {
    console.log(this.isVideoVisible)
    // If video becomes visible, start capturing video
    if (this.isVideoVisible) {
      this.isVideoVisible =false;
    
    } else {
      this.isVideoVisible = true;
      // If video is hidden, stop video capture
    }
  }

  private handleVisibilityChange = () => {
    if (document.visibilityState === 'visible') {
      console.log('Tab is active');
    } else {
      alert('AFSHTK YA CHEATER');
      console.log('Tab is inactive');
    }
  };

  private async startDetection() {
    await Promise.all([
      faceapi.nets.tinyFaceDetector.loadFromUri('/assets/models'),
      faceapi.nets.faceLandmark68Net.loadFromUri('/assets/models'),
      faceapi.nets.faceRecognitionNet.loadFromUri('/assets/models'),
      faceapi.nets.faceExpressionNet.loadFromUri('/assets/models')
    ]);

    navigator.mediaDevices.getUserMedia({ video: {} })
      .then(stream => {
        this.videoEl.nativeElement.srcObject = stream;
        this.videoEl.nativeElement.addEventListener('play', async () => {
          const displaySize = { width: this.videoEl.nativeElement.videoWidth, height: this.videoEl.nativeElement.videoHeight };
          const canvas = this.canvasEl.nativeElement;
          canvas.width = displaySize.width;
          canvas.height = displaySize.height;
          const ctx = canvas.getContext('2d');
          if (!ctx) {
            console.error('Failed to get canvas context');
            return;
          }
          const model = await cocoSsd.load();

          this.detectionInterval = setInterval(async () => {
            if (!this.videoEl.nativeElement) return;

            const currentTime = Date.now();
            if (currentTime - this.lastFrameTime >= 2000) {
              ctx.drawImage(this.videoEl.nativeElement, 0, 0, canvas.width, canvas.height);
              const imageData = canvas.toDataURL('image/jpeg', 0.8);
              this.socket.emit('image', imageData);
              this.lastFrameTime = currentTime;
            }

            const detections = await faceapi
              .detectAllFaces(this.videoEl.nativeElement, new faceapi.TinyFaceDetectorOptions())
              .withFaceLandmarks()
              .withFaceExpressions();

            ctx.clearRect(0, 0, canvas.width, canvas.height);

            if (this.videoEl.nativeElement) {
              const predictions = await model.detect(this.videoEl.nativeElement);
              predictions.forEach(prediction => {
                if (prediction.class === 'cell phone') {
                  ctx.beginPath();
                  ctx.rect(...prediction.bbox);
                  ctx.lineWidth = 2;
                  ctx.strokeStyle = 'red';
                  ctx.fillStyle = 'red';
                  ctx.stroke();
                  ctx.fillText('Phone detected!', prediction.bbox[0], prediction.bbox[1] > 10 ? prediction.bbox[1] - 5 : 10);
                  alert('PUT DOWN YOUR PHONE CHEATER');
                }
              });
            } else {
              console.error('Video element is not available');
            }

            if (detections.length === 0) {
              console.log('User is absent');
            } else if (detections.length > 1) {
              alert('More than one person detected');
            }
          }, 100);
        });
      })
      .catch(err => console.error(err));
  }

  private stopFaceDetection() {
    clearInterval(this.detectionInterval);
    if (this.videoEl.nativeElement && this.videoEl.nativeElement.srcObject) {
      const tracks = (this.videoEl.nativeElement.srcObject as MediaStream).getTracks();
      tracks.forEach(track => track.stop());
    }
    const ctx = this.canvasEl.nativeElement.getContext('2d');
    if (ctx) {
      ctx.clearRect(0, 0, this.canvasEl.nativeElement.width, this.canvasEl.nativeElement.height);
    }
  }

  private handleSocket() {
    this.socket = io('http://localhost:5000'); // Replace with your server URL

    this.socket.on('response_back', async (data: any) => {
      this.displayText = data.text;
      console.log(data.text);

      if (data.text !== 'Forward') {
        const formattedDate = dayjs().format('YYYY-MM-DD_HH-mm-ss');

        try {
          const blob = await fetch(data.image).then(res => res.blob());
          const file = new File([blob], `processedimage_${formattedDate}.jpg`, { type: 'image/jpeg' });

          this.proctoredVideoService.savePhoto(2, 12, file).subscribe(
            response => {
              console.log('Photo saved successfully', response);
            },
            error => {
              console.error('Failed to save photo', error);
            }
          );
        } catch (error) {
          console.error('Failed to fetch or process image:', error);
        }
      }
    });
  }
}
