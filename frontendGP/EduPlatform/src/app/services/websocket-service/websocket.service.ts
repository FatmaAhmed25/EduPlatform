// // src/app/services/websocket/websocket.service.ts
// import { Injectable } from '@angular/core';
// import { Client } from '@stomp/stompjs';
// import * as SockJS from 'sockjs-client';

// @Injectable({
//   providedIn: 'root'
// })
// export class WebSocketService {
//   private stompClient: Client | null = null;

//   connect(token: string, callback: () => void): void {
//     const socket = new SockJS('http://localhost:8080/ws');
//     this.stompClient = new Client({
//       webSocketFactory: () => socket,
//       connectHeaders: { Authorization: `Bearer ${token}` },
//       onConnect: frame => {
//         console.log('Connected: ' + frame);
//         callback();
//       },
//       debug: str => {
//         console.log(new Date(), str);
//       }
//     });

//     this.stompClient.activate();
//   }

//   subscribe(topic: string, callback: (message: any) => void): void {
//     if (this.stompClient) {
//       this.stompClient.subscribe(topic, message => {
//         callback(message);
//       });
//     }
//   }

//   send(destination: string, body: any): void {
//     if (this.stompClient) {
//       this.stompClient.publish({ destination, body: JSON.stringify(body) });
//     }
//   }

//   disconnect(): void {
//     if (this.stompClient) {
//       this.stompClient.deactivate();
//     }
//   }
// }
