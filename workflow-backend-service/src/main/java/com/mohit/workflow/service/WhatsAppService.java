//package com.mohit.workflow.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class WhatsAppService {
//
//    @Value("${app.notification.whatsapp.api-url}")
//    private String whatsAppApiUrl;
//
//    @Value("${app.notification.whatsapp.token}")
//    private String whatsAppToken;
//
//    public void sendMessage(String phoneNumber, String message) {
//        // Implementation would use WhatsApp Business API
//        // For now, just log the message
//        System.out.println("WhatsApp to " + phoneNumber + ": " + message);
//    }
//}