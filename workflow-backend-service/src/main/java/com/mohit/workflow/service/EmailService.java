//package com.mohit.workflow.service;
//
//import com.mohit.workflow.exception.NotificationException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import org.springframework.beans.factory.annotation.Value;
//
//@Service
//public class EmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${app.notification.email.from}")
//    private String fromEmail;
//
//    public void sendEmail(String to, String subject, String body) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            helper.setFrom(fromEmail);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(body, true); // true for HTML content
//
//            mailSender.send(message);
//        } catch (Exception e) {
//            throw new NotificationException("Failed to send email", e);
//        }
//    }
//}