//package com.mohit.workflow.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Locale;
//import java.util.Map;
//
//@Service
//public class NotificationTemplateService {
//
//    @Autowired
//    private TemplateEngine templateEngine;
//
//    public String generateEmailSubject(String templateName, Map<String, Object> data) {
//        return templateEngine.process(templateName + "_subject", new Context(Locale.getDefault(), data));
//    }
//
//    public String generateEmailBody(String templateName, Map<String, Object> data) {
//        return templateEngine.process(templateName + "_email", new Context(Locale.getDefault(), data));
//    }
//
//    public String generateSmsMessage(String templateName, Map<String, Object> data) {
//        return templateEngine.process(templateName + "_sms", new Context(Locale.getDefault(), data));
//    }
//
//    public String generateWhatsAppMessage(String templateName, Map<String, Object> data) {
//        return templateEngine.process(templateName + "_whatsapp", new Context(Locale.getDefault(), data));
//    }
//
//    public String generateNotificationTitle(String templateName, Map<String, Object> data) {
//        return templateEngine.process(templateName + "_title", new Context(Locale.getDefault(), data));
//    }
//
//    public String generateNotificationMessage(String templateName, Map<String, Object> data) {
//        return templateEngine.process(templateName + "_notification", new Context(Locale.getDefault(), data));
//    }
//}