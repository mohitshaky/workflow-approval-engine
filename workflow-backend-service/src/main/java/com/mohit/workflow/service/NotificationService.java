//package com.mohit.workflow.service;
//
//import com.mohit.workflow.entity.*;
//import org.apache.catalina.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class NotificationService {
//
//    @Autowired
//    private EmailService emailService;
//
//    @Autowired
//    private SmsService smsService;
//
//    @Autowired
//    private WhatsAppService whatsAppService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private NotificationTemplateService templateService;
//
//    @Autowired
//    private NotificationPreferenceService preferenceService;
//
//    /**
//     * Send approval notification to approvers
//     */
//    @Async
//    public void sendApprovalNotification(Long approverId, WorkflowInstance instance,
//                                         WorkflowStep step, WorkflowApproval approval) {
//        User approver = userRepository.findById(approverId)
//            .orElseThrow(() -> new UserNotFoundException("Approver not found"));
//
//        // Get notification preferences
//        NotificationPreference preference = preferenceService.getUserPreference(approverId);
//
//        // Prepare notification data
//        Map<String, Object> templateData = prepareApprovalTemplateData(instance, step, approval, approver);
//
//        // Send notifications based on preferences and priority
//        if (preference.isEmailEnabled() || instance.getPriority() == WorkflowPriority.HIGH) {
//            sendEmailNotification(approver, "APPROVAL_REQUEST", templateData);
//        }
//
//        if (preference.isSmsEnabled() && instance.getPriority() == WorkflowPriority.CRITICAL) {
//            sendSmsNotification(approver, "APPROVAL_REQUEST_SMS", templateData);
//        }
//
//        if (preference.isWhatsAppEnabled() && instance.getPriority() == WorkflowPriority.CRITICAL) {
//            sendWhatsAppNotification(approver, "APPROVAL_REQUEST_WHATSAPP", templateData);
//        }
//
//        // Send in-app notification
//        sendInAppNotification(approver, "APPROVAL_REQUEST", templateData);
//    }
//
//    /**
//     * Send workflow completion notification
//     */
//    @Async
//    public void sendWorkflowCompletionNotification(WorkflowInstance instance) {
//        User initiatedBy = userRepository.findById(instance.getInitiatedBy())
//            .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//        NotificationPreference preference = preferenceService.getUserPreference(instance.getInitiatedBy());
//        Map<String, Object> templateData = prepareCompletionTemplateData(instance);
//
//        // Always send completion notifications
//        if (preference.isEmailEnabled()) {
//            sendEmailNotification(initiatedBy, "WORKFLOW_COMPLETED", templateData);
//        }
//
//        sendInAppNotification(initiatedBy, "WORKFLOW_COMPLETED", templateData);
//
//        // Notify other stakeholders based on entity type
//        notifyStakeholders(instance, "WORKFLOW_COMPLETED", templateData);
//    }
//
//    /**
//     * Send workflow rejection notification
//     */
//    @Async
//    public void sendWorkflowRejectionNotification(WorkflowInstance instance, String reason) {
//        User initiatedBy = userRepository.findById(instance.getInitiatedBy())
//            .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//        Map<String, Object> templateData = prepareRejectionTemplateData(instance, reason);
//
//        // Always send rejection notifications
//        sendEmailNotification(initiatedBy, "WORKFLOW_REJECTED", templateData);
//        sendInAppNotification(initiatedBy, "WORKFLOW_REJECTED", templateData);
//
//        // Notify manager if applicable
//        notifyManager(instance, "WORKFLOW_REJECTED", templateData);
//    }
//
//    /**
//     * Send escalation notification
//     */
//    @Async
//    public void sendEscalationNotification(WorkflowInstance instance, WorkflowStep step,
//                                         List<Long> escalationApprovers) {
//        Map<String, Object> templateData = prepareEscalationTemplateData(instance, step);
//
//        for (Long approverId : escalationApprovers) {
//            User approver = userRepository.findById(approverId).orElse(null);
//            if (approver != null) {
//                sendEmailNotification(approver, "WORKFLOW_ESCALATED", templateData);
//                sendInAppNotification(approver, "WORKFLOW_ESCALATED", templateData);
//
//                // Send SMS for critical escalations
//                if (instance.getPriority() == WorkflowPriority.CRITICAL) {
//                    sendSmsNotification(approver, "WORKFLOW_ESCALATED_SMS", templateData);
//                }
//            }
//        }
//    }
//
//    /**
//     * Send reminder notifications for pending approvals
//     */
//    @Async
//
//
//    // =====================================================
//    // PRIVATE HELPER METHODS
//    // =====================================================
//
//    private void sendEmailNotification(User user, String templateName, Map<String, Object> templateData) {
//        try {
//            String subject = templateService.generateEmailSubject(templateName, templateData);
//            String body = templateService.generateEmailBody(templateName, templateData);
//
//            emailService.sendEmail(user.getEmail(), subject, body);
//
//            // Log notification
//            logNotification(user.getId(), "EMAIL", templateName, "SENT");
//        } catch (Exception e) {
//            logNotification(user.getId(), "EMAIL", templateName, "FAILED");
//            // Log error but don't throw to avoid disrupting workflow
//        }
//    }
//
//    private void sendSmsNotification(User user, String templateName, Map<String, Object> templateData) {
//        try {
//            String message = templateService.generateSmsMessage(templateName, templateData);
//            smsService.sendSms(user.getPhone(), message);
//
//            logNotification(user.getId(), "SMS", templateName, "SENT");
//        } catch (Exception e) {
//            logNotification(user.getId(), "SMS", templateName, "FAILED");
//        }
//    }
//
//    private void sendWhatsAppNotification(User user, String templateName, Map<String, Object> templateData) {
//        try {
//            String message = templateService.generateWhatsAppMessage(templateName, templateData);
//            whatsAppService.sendMessage(user.getPhone(), message);
//
//            logNotification(user.getId(), "WHATSAPP", templateName, "SENT");
//        } catch (Exception e) {
//            logNotification(user.getId(), "WHATSAPP", templateName, "FAILED");
//        }
//    }
//
//    private void sendInAppNotification(User user, String templateName, Map<String, Object> templateData) {
//        try {
//            String title = templateService.generateNotificationTitle(templateName, templateData);
//            String message = templateService.generateNotificationMessage(templateName, templateData);
//
//            InAppNotification notification = new InAppNotification();
//            notification.setUserId(user.getId());
//            notification.setTitle(title);
//            notification.setMessage(message);
//            notification.setTemplateData(templateData);
//            notification.setRead(false);
//
//            inAppNotificationRepository.save(notification);
//
//            logNotification(user.getId(), "IN_APP", templateName, "SENT");
//        } catch (Exception e) {
//            logNotification(user.getId(), "IN_APP", templateName, "FAILED");
//        }
//    }
//
//    private void notifyStakeholders(WorkflowInstance instance, String templateName, Map<String, Object> templateData) {
//        // Implementation depends on entity type and business rules
//        switch (instance.getEntityType()) {
//            case "STOCK_UPDATE":
//                notifyStockUpdateStakeholders(instance, templateData);
//                break;
//            case "BUYER_ONBOARDING":
//                notifyBuyerOnboardingStakeholders(instance, templateData);
//                break;
//            // Add more entity types as needed
//        }
//    }
//
//    private void notifyStockUpdateStakeholders(WorkflowInstance instance, Map<String, Object> templateData) {
//        // Notify warehouse team, finance team, etc.
//        List<Long> warehouseTeam = userWorkflowRoleRepository
//            .findUserIdsByRoleAndEntityType("WAREHOUSE_MANAGER", "STOCK_UPDATE");
//
//        for (Long userId : warehouseTeam) {
//            User user = userRepository.findById(userId).orElse(null);
//            if (user != null) {
//                sendEmailNotification(user, "STOCK_UPDATE_COMPLETED", templateData);
//            }
//        }
//    }
//
//    private void notifyBuyerOnboardingStakeholders(WorkflowInstance instance, Map<String, Object> templateData) {
//        // Notify sales team, account managers, etc.
//        List<Long> salesTeam = userWorkflowRoleRepository
//            .findUserIdsByRoleAndEntityType("SALES_MANAGER", "BUYER_ONBOARDING");
//
//        for (Long userId : salesTeam) {
//            User user = userRepository.findById(userId).orElse(null);
//            if (user != null) {
//                sendEmailNotification(user, "BUYER_ONBOARDED", templateData);
//            }
//        }
//    }
//
//    private void notifyManager(WorkflowInstance instance, String templateName, Map<String, Object> templateData) {
//        // Find manager based on entity type or initiator's hierarchy
//        // Implementation depends on organizational structure
//    }
//
//    private Map<String, Object> prepareApprovalTemplateData(WorkflowInstance instance, WorkflowStep step,
//                                                           WorkflowApproval approval, User approver) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("approverName", approver.getFirstName() + " " + approver.getLastName());
//        data.put("workflowType", instance.getEntityType());
//        data.put("stepName", step.getStepName());
//        data.put("priority", instance.getPriority().toString());
//        data.put("initiatedAt", instance.getInitiatedAt());
//        data.put("entityData", instance.getMetadata());
//        data.put("approvalUrl", generateApprovalUrl(approval.getId()));
//        return data;
//    }
//
//    private Map<String, Object> prepareCompletionTemplateData(WorkflowInstance instance) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("workflowType", instance.getEntityType());
//        data.put("completedAt", instance.getCompletedAt());
//        data.put("entityData", instance.getMetadata());
//        return data;
//    }
//
//    private Map<String, Object> prepareRejectionTemplateData(WorkflowInstance instance, String reason) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("workflowType", instance.getEntityType());
//        data.put("rejectionReason", reason);
//        data.put("entityData", instance.getMetadata());
//        return data;
//    }
//
//    private Map<String, Object> prepareEscalationTemplateData(WorkflowInstance instance, WorkflowStep step) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("workflowType", instance.getEntityType());
//        data.put("stepName", step.getStepName());
//        data.put("priority", instance.getPriority().toString());
//        data.put("overdueHours", calculateOverdueHours(instance, step));
//        data.put("entityData", instance.getMetadata());
//        return data;
//    }
//
//    private Map<String, Object> prepareReminderTemplateData(WorkflowInstance instance, WorkflowApproval approval) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("workflowType", instance.getEntityType());
//        data.put("pendingHours", calculatePendingHours(approval));
//        data.put("priority", instance.getPriority().toString());
//        data.put("approvalUrl", generateApprovalUrl(approval.getId()));
//        return data;
//    }
//
//    private String generateApprovalUrl(Long approvalId) {
//        return "https://yourb2bplatform.com/approvals/" + approvalId;
//    }
//
//    private long calculateOverdueHours(WorkflowInstance instance, WorkflowStep step) {
//        // Calculate based on step timeout and current time
//        return 0; // Implementation details
//    }
//
//    private long calculatePendingHours(WorkflowApproval approval) {
//        // Calculate based on approval creation time
//        return 0; // Implementation details
//    }
//
//    private void logNotification(Long userId, String channel, String templateName, String status) {
//        NotificationLog log = new NotificationLog();
//        log.setUserId(userId);
//        log.setChannel(channel);
//        log.setTemplateName(templateName);
//        log.setStatus(status);
//        log.setSentAt(LocalDateTime.now());
//
//        // Save to notification log repository
//        // notificationLogRepository.save(log);
//    }
//}