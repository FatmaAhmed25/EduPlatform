import { Injectable } from '@angular/core';
import { Client, StompSubscription } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private stompClient: Client | null = null;
  private isConnected = false;

  constructor() {}

  connect(token: string, callback: () => void): void {
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      connectHeaders: { Authorization: `Bearer ${token}` },
      onConnect: frame => {
        console.log('Connected: ' + frame);
        this.isConnected = true;
        callback();
      },
      debug: str => {
        console.log(new Date(), str);
      }
    });

    this.stompClient.activate();
  }

  send(destination: string, body: any): void {
    if (this.isConnected && this.stompClient) {
      this.stompClient.publish({ destination, body: JSON.stringify(body) });
    } else {
      console.error('WebSocket is not connected.');
    }
  }

  subscribeToComments(topic: string, callback: (message: any) => void): StompSubscription | null {
    if (this.stompClient) {
      return this.stompClient.subscribe(topic, message => {
        callback(JSON.parse(message.body)); // Parse message body assuming it's JSON
      });
    }
    return null;
  }
  subscribeToNotificationsForCourse(courseId: number, callback: (message: any) => void): StompSubscription | null {
    if (this.stompClient) {
      const topic = `/topic/course/${courseId}`;
      return this.stompClient.subscribe(topic, message => {
        callback(JSON.parse(message.body)); // Parse message body assuming it's JSON
      });
    }
    return null;
  }

  disconnect(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.isConnected = false;
    }
  }
}
