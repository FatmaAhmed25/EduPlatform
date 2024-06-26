//package com.edu.eduplatform.listeners;
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class NotificationListener {
//
//    @RabbitListener(queues = "notificationQueue")
//    public void receiveMessage(String message) {
//        // Implement your notification logic here (e.g., send email, SMS, etc.)
//        System.out.println("Received message: " + message);
//    }
//}
