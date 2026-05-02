package com.mohit.workflow.entity;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;
@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true)
    private Long userId;
    
    @Column(name = "email_enabled")
    private boolean emailEnabled = true;
    
    @Column(name = "sms_enabled")
    private boolean smsEnabled = false;
    
    @Column(name = "whatsapp_enabled")
    private boolean whatsAppEnabled = false;
    
    @Column(name = "in_app_enabled")
    private boolean inAppEnabled = true;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    
    public boolean isSmsEnabled() { return smsEnabled; }
    public void setSmsEnabled(boolean smsEnabled) { this.smsEnabled = smsEnabled; }
    
    public boolean isWhatsAppEnabled() { return whatsAppEnabled; }
    public void setWhatsAppEnabled(boolean whatsAppEnabled) { this.whatsAppEnabled = whatsAppEnabled; }
    
    public boolean isInAppEnabled() { return inAppEnabled; }
    public void setInAppEnabled(boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }
}