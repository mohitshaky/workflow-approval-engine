package com.mohit.workflow.service;

import com.mohit.workflow.dto.RuleApplicationLog;
import com.mohit.workflow.entity.*;
import com.mohit.workflow.exception.NoApproversFoundException;
import com.mohit.workflow.repository.ApprovalRuleRepository;
import com.mohit.workflow.repository.UserWorkflowRoleRepository;
import com.mohit.workflow.repository.WorkflowDefinitionRepository;
import com.mohit.workflow.repository.WorkflowStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkflowRuleEngine {
    
    @Autowired
    private ApprovalRuleRepository approvalRuleRepository;
    
    @Autowired
    private UserWorkflowRoleRepository userWorkflowRoleRepository;

    @Autowired
    WorkflowStepRepository workflowStepRepository;

    
    /**
     * Determine approvers for a workflow step based on configured rules
     */
    public List<Long> determineApprovers(WorkflowInstance instance, WorkflowStep step) {
        Set<Long> approverIds = new HashSet<>();

        // Get base approvers from step configuration
        List<String> requiredRoles = step.getRequiredRoles();
        if (requiredRoles != null && !requiredRoles.isEmpty()) {
            for (String role : requiredRoles) {
                List<Long> roleApprovers = getUsersByRole(role, instance.getEntityType());
                approverIds.addAll(roleApprovers);
            }
        } // Apply dynamic rules based on entity data
        List<ApprovalRule> rules = approvalRuleRepository
                .findByWorkflowDefinitionIdAndIsActiveOrderByApprovalLevel(
                        instance.getWorkflowDefinitionId(), true);

        for (ApprovalRule rule : rules) {
            if (evaluateRule(rule, instance)) {
                List<Long> ruleApprovers = getUsersByRole(
                        rule.getRequiredApproverRole(), instance.getEntityType());
                approverIds.addAll(ruleApprovers);
            }
        }

        return new ArrayList<>(approverIds);
    }

    
    /**
     * Get approvers based on dynamic business rules
//     */
//    private List<Long> getApproversFromRules(WorkflowInstance instance, WorkflowStep step) {
//        List<ApprovalRule> rules = approvalRuleRepository
//            .findByWorkflowDefinitionIdAndIsActiveOrderByApprovalLevel(
//                instance.getWorkflowDefinitionId(), true);
//
//        Set<Long> ruleBasedApprovers = new HashSet<>();
//
//        for (ApprovalRule rule : rules) {
//            if (evaluateRule(rule, instance)) {
//                List<Long> ruleApprovers = getUsersByRole(
//                    rule.getRequiredApproverRole(), instance.getEntityType());
//                ruleBasedApprovers.addAll(ruleApprovers);
//
//                // Log rule application
//                logRuleApplication(instance.getId(), rule, true);
//            } else {
//                // Log rule evaluation
//                logRuleApplication(instance.getId(), rule, false);
//            }
//        }
//
//        return new ArrayList<>(ruleBasedApprovers);
//    }
//
//    /**
//     * Get escalation approvers if conditions are met
//     */
//    private List<Long> getEscalationApprovers(WorkflowInstance instance, WorkflowStep step) {
//        List<Long> escalationApprovers = new ArrayList<>();
//
//        // Check if workflow is high priority or overdue
//        if (instance.getPriority() == WorkflowPriority.CRITICAL ||
//            isWorkflowOverdue(instance)) {
//
//            // Add super admin or escalation roles
//            escalationApprovers.addAll(getUsersByRole("SUPER_ADMIN", "GLOBAL"));
//
//            if (step.getEscalationStepId() != null) {
//                // Get approvers from escalation step
//                WorkflowStep escalationStep = workflowStepRepository
//                    .findById(step.getEscalationStepId())
//                    .orElse(null);
//
//                if (escalationStep != null) {
//                    escalationApprovers.addAll(getBaseApprovers(escalationStep, instance.getEntityType()));
//                }
//            }
//        }
//
//        return escalationApprovers;
//    }
    
    /**
     * Evaluate if a rule applies to the current instance
     */
    boolean evaluateRule(ApprovalRule rule, WorkflowInstance instance) {
        if (rule.getEntityField() == null || rule.getOperator() == null) {
            return false;
        }

        // Get entity data from metadata
        Map<String, Object> metadata = instance.getMetadata();
        if (metadata == null) {
            return false;
        }

        Object fieldValue = metadata.get(rule.getEntityField());
        if (fieldValue == null) {
            return false;
        }

        return evaluateCondition(fieldValue, rule.getOperator(), rule.getThresholdValue());
    }
    
    /**
     * Evaluate condition based on operator
     */
    private boolean evaluateCondition(Object fieldValue, String operator, String thresholdValue) {
        try {
            switch (operator.toUpperCase()) {
                case "GT":
                    return Double.parseDouble(fieldValue.toString()) >
                            Double.parseDouble(thresholdValue);
                case "LT":
                    return Double.parseDouble(fieldValue.toString()) <
                            Double.parseDouble(thresholdValue);
                case "EQ":
                    return fieldValue.toString().equals(thresholdValue);
                case "IN":
                    String[] values = thresholdValue.split(",");
                    return Arrays.asList(values).contains(fieldValue.toString());
                case "BETWEEN":
                    String[] range = thresholdValue.split(",");
                    double value = Double.parseDouble(fieldValue.toString());
                    return value >= Double.parseDouble(range[0]) &&
                            value <= Double.parseDouble(range[1]);
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Compare numeric values
     */
//    private int compareNumeric(Object fieldValue, String thresholdValue) {
//        BigDecimal fieldDecimal = new BigDecimal(fieldValue.toString());
//        BigDecimal thresholdDecimal = new BigDecimal(thresholdValue);
//        return fieldDecimal.compareTo(thresholdDecimal);
//    }
    
    /**
     * Evaluate IN condition
     */
//    private boolean evaluateInCondition(Object fieldValue, String thresholdValue) {
//        String[] values = thresholdValue.split(",");
//        String fieldStr = fieldValue.toString().trim();
//        return Arrays.stream(values)
//            .map(String::trim)
//            .anyMatch(v -> v.equals(fieldStr));
//    }
    
    /**
     * Evaluate BETWEEN condition
     */
//    private boolean evaluateBetweenCondition(Object fieldValue, String thresholdValue) {
//        String[] range = thresholdValue.split(",");
//        if (range.length != 2) {
//            return false;
//        }
//
//        try {
//            BigDecimal value = new BigDecimal(fieldValue.toString());
//            BigDecimal min = new BigDecimal(range[0].trim());
//            BigDecimal max = new BigDecimal(range[1].trim());
//            return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
    
    /**
     * Get nested value from metadata using dot notation
     */
//    private Object getNestedValue(Map<String, Object> metadata, String fieldPath) {
//        String[] pathParts = fieldPath.split("\\.");
//        Object current = metadata;
//
//        for (String part : pathParts) {
//            if (current instanceof Map) {
//                current = ((Map<?, ?>) current).get(part);
//            } else {
//                return null;
//            }
//
//            if (current == null) {
//                return null;
//            }
//        }
//
//        return current;
//    }
    
    /**
     * Get users by workflow role
     */
    @Cacheable(value = "usersByRole", key = "#role + '_' + #entityType")
    private List<Long> getUsersByRole(String role, String entityType) {
        // First, try to get users with specific entity type scope
        List<Long> specificUsers = userWorkflowRoleRepository
            .findUserIdsByRoleAndEntityType(role, entityType);
        
        // If no specific users found, get global role users
        if (specificUsers.isEmpty()) {
            specificUsers = userWorkflowRoleRepository
                .findUserIdsByRoleAndEntityType(role, "GLOBAL");
        }
        
        return specificUsers;
    }
    
    /**
     * Validate approvers and filter out invalid ones
     */
//    private List<Long> validateApprovers(List<Long> approverIds, String entityType, WorkflowStep step) {
//        if (approverIds.isEmpty()) {
//            return approverIds;
//        }
//
//        // Get user details and filter active ones
//        List<User> users = userRepository.findAllById(approverIds);
//
//        return users.stream()
//            .filter(user -> user.getStatus() == UserStatus.ACTIVE)
//            .filter(user -> hasApprovalPermission(user.getId(), entityType, step))
//            .map(User::getId)
//            .collect(Collectors.toList());
//    }
    
//    /**
//     * Check if user has permission to approve for this entity type and step
//     */
//    private boolean hasApprovalPermission(Long userId, String entityType, WorkflowStep step) {
//        // Check if user has any of the required roles for this step
//        List<String> requiredRoles = step.getRequiredRoles();
//        if (requiredRoles == null || requiredRoles.isEmpty()) {
//            return true; // No specific role required
//        }
//
//        List<UserWorkflowRole> userRoles = userWorkflowRoleRepository
//            .findByUserIdAndIsActive(userId, true);
//
//        return userRoles.stream()
//            .anyMatch(role ->
//                requiredRoles.contains(role.getRoleName()) &&
//                (role.getEntityType().equals(entityType) || role.getEntityType().equals("GLOBAL"))
//            );
//    }
//
//    /**
//     * Check if workflow is overdue
//     */
//    private boolean isWorkflowOverdue(WorkflowInstance instance) {
//        // Get total expected processing time for the workflow
//        int totalExpectedHours = workflowStepRepository
//            .findByWorkflowDefinitionId(instance.getWorkflowDefinitionId())
//            .stream()
//            .mapToInt(step -> step.getTimeoutHours() != null ? step.getTimeoutHours() : 24)
//            .sum();
//
//        LocalDateTime expectedCompletion = instance.getInitiatedAt().plusHours(totalExpectedHours);
//        return LocalDateTime.now().isAfter(expectedCompletion);
//    }
//
//    /**
//     * Log rule application for audit purposes
//     */
//    private void logRuleApplication(Long instanceId, ApprovalRule rule, boolean applied) {
//        RuleApplicationLog log = new RuleApplicationLog();
//        log.setWorkflowInstanceId(instanceId);
//        log.setRuleId(rule.getId());
//        log.setRuleName(rule.getRuleName());
//        log.setApplied(applied);
//        log.setEvaluatedAt(LocalDateTime.now());
//
//        // This would be saved to a rule application log table
//        // ruleApplicationLogRepository.save(log);
//    }
//
//    /**
//     * Log rule evaluation errors
//     */
//    private void logRuleEvaluationError(Object fieldValue, String operator, String thresholdValue, Exception error) {
//        // Log to application logs or error tracking system
//        System.err.println("Rule evaluation error: " +
//            "fieldValue=" + fieldValue +
//            ", operator=" + operator +
//            ", threshold=" + thresholdValue +
//            ", error=" + error.getMessage());
//    }
}


