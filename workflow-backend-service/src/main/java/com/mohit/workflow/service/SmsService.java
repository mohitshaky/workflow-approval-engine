//package com.mohit.workflow.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SmsService {
//
//    @Value("${app.notification.sms.provider}")
//    private String smsProvider;
//
//    @Value("${app.notification.sms.api-key}")
//    private String apiKey;
//
//    public void sendSms(String phoneNumber, String message) {
//        // Implementation would use Twilio, AWS SNS, or other SMS provider
//        // For now, just log the SMS
//        System.out.println("SMS to " + phoneNumber + ": " + message);
//    }
//}