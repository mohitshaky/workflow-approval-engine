package com.mohit.workflow.service;

import com.mohit.workflow.dto.InitiateWorkflowRequest;
import com.mohit.workflow.dto.ProcessApprovalRequest;
import com.mohit.workflow.dto.WorkflowInstanceDTO;
import com.mohit.workflow.entity.*;
import com.mohit.workflow.events.*;
import com.mohit.workflow.exception.*;
import com.mohit.workflow.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.*;

// =====================================================
// MAIN WORKFLOW ENGINE SERVICE
// =====================================================

@Service
@Transactional
public class WorkflowEngine {
    
    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;
    
    @Autowired
    private WorkflowDefinitionRepository workflowDefinitionRepository;
    
    @Autowired
    private WorkflowStepRepository workflowStepRepository;
    
    @Autowired
    private WorkflowApprovalRepository workflowApprovalRepository;
    
    @Autowired
    private WorkflowHistoryRepository workflowHistoryRepository;

    @Autowired
    private WorkflowRuleEngine ruleEngine;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    /**
     * Initiate a new workflow instance
     */
    public WorkflowInstanceDTO initiateWorkflow(InitiateWorkflowRequest request) throws JsonProcessingException {

        WorkflowDefinition workflow = workflowDefinitionRepository
                .findByEntityTypeAndIsActive(request.getEntityType(), true)
                .orElseThrow(() -> new WorkflowNotFoundException(
                        STR."No active workflow found for entity type: \{request.getEntityType()}"));

        // Check if workflow already exists for this entity
        List<WorkflowInstance> existingInstance = workflowInstanceRepository
                .findByEntityIdAndTypeAndStatus(
                        request.getEntityId(),
                        request.getEntityType(),
                        String.valueOf(WorkflowStatus.PENDING));

        if (!existingInstance.isEmpty()) {
            throw new WorkflowAlreadyExistsException(
                    "Workflow already in progress for this entity");
        }

        // Create new workflow instance

        WorkflowInstance instance = new WorkflowInstance();
        instance.setWorkflowDefinitionId(workflow.getId());
        instance.setEntityId(request.getEntityId());
        instance.setEntityType(request.getEntityType());
        instance.setStatus(WorkflowStatus.PENDING);
        instance.setPriority(request.getPriority());
        instance.setInitiatedBy(request.getInitiatedBy());
        instance.setMetadata(request.getMetadata());

        // Get first step
        WorkflowStep firstStep = workflowStepRepository
                .findFirstStepByWorkflowDefinitionId(workflow.getId())
                .orElseThrow(() -> new WorkflowConfigurationException(
                        "No steps configured for workflow"));

        instance.setCurrentStepId(firstStep.getId());

        WorkflowInstance savedInstance = workflowInstanceRepository.saveNative(instance);

        // Log workflow initiation
        logWorkflowHistory(savedInstance.getId(), "WORKFLOW_INITIATED",
                request.getInitiatedBy(), null, WorkflowStatus.PENDING.toString(),
                firstStep.getStepName(), "Workflow initiated");

        // Process first step
        processCurrentStep(savedInstance);

        return convertToDTO(savedInstance);
    }


    /**
     * Process approval decision
     */
    public void processApproval(ProcessApprovalRequest request) {
        WorkflowApproval approval = workflowApprovalRepository
                .findById(request.getApprovalId())
                .orElseThrow(() -> new ApprovalNotFoundException("Approval not found"));

        if (approval.getStatus() != ApprovalStatus.PENDING) {
            throw new InvalidApprovalStateException("Approval already processed");
        }

        // Update approval
        workflowApprovalRepository.updateApprovalStatusNative(
                approval.getId(),
                request.getDecision().name(), // Enum to String
                LocalDateTime.now(),
                request.getComments(),
                LocalDateTime.now()
        );

        // Log approval action
        WorkflowInstance instance = workflowInstanceRepository
                .findById(approval.getWorkflowInstanceId())
                .orElseThrow(() -> new WorkflowInstanceNotFoundException("Instance not found"));

        logWorkflowHistory(instance.getId(),
                request.getDecision() == ApprovalStatus.APPROVED ? "APPROVED" : "REJECTED",
                request.getApproverId(),
                instance.getStatus().toString(),
                request.getDecision().toString(),
                null, request.getComments());

        // Check if step is complete
        boolean stepComplete = checkStepCompletion(approval.getWorkflowStepId(),
                approval.getWorkflowInstanceId());

        if (stepComplete) {
            if (request.getDecision() == ApprovalStatus.APPROVED) {
                moveToNextStep(instance);
            } else {
                rejectWorkflow(instance, request.getComments());
            }
        }
    }
    
    /**
     * Process current step of workflow
     */
    private void processCurrentStep(WorkflowInstance instance) {
        WorkflowStep currentStep = workflowStepRepository
                .findById(instance.getCurrentStepId())
                .orElseThrow(() -> new WorkflowStepNotFoundException("Step not found"));

        switch (currentStep.getStepType()) {
            case APPROVAL:
                processApprovalStep(instance, currentStep);
                break;
            case NOTIFICATION:
                processNotificationStep(instance, currentStep);
                moveToNextStep(instance);
                break;
            case SYSTEM_ACTION:
                processSystemActionStep(instance, currentStep);
                moveToNextStep(instance);
                break;
        }
    }

    /**
     * Process approval step
     */
    private void processApprovalStep(WorkflowInstance instance, WorkflowStep step) {
        // Apply approval rules to determine required approvers
        List<Long> approverIds = ruleEngine.determineApprovers(instance, step);

        if (approverIds.isEmpty()) {
            throw new WorkflowConfigurationException(
                    "No approvers found for step: " + step.getStepName());
        }

        // Create approval records
        for (Long approverId : approverIds) {
            WorkflowApproval approval = new WorkflowApproval();
            approval.setWorkflowInstanceId(instance.getId());
            approval.setWorkflowStepId(step.getId());
            approval.setApproverId(approverId);
            approval.setStatus(ApprovalStatus.PENDING);
            workflowApprovalRepository.insertApproval(approval);

        }

        // Schedule escalation if configured
        if (step.getTimeoutHours() != null && step.getEscalationStepId() != null) {
            scheduleEscalation(instance.getId(), step.getId(), step.getTimeoutHours());
        }
    }
    /**
     * Process notification step
     */
    private void processNotificationStep(WorkflowInstance instance, WorkflowStep step) {
        // Publish notification event
        eventPublisher.publishEvent(new WorkflowNotificationEvent(instance, step));

        logWorkflowHistory(instance.getId(), "NOTIFICATION_SENT", null,
            instance.getStatus().toString(), instance.getStatus().toString(),
            step.getStepName(), "Notification sent");
    }

    /**
     * Process system action step
     */
    private void processSystemActionStep(WorkflowInstance instance, WorkflowStep step) {
        // Publish system action event
        eventPublisher.publishEvent(new WorkflowSystemActionEvent(instance, step));

        logWorkflowHistory(instance.getId(), "SYSTEM_ACTION_EXECUTED", null,
            instance.getStatus().toString(), instance.getStatus().toString(),
            step.getStepName(), "System action executed");
    }

    /**
     * Move workflow to next step
     */
    private void moveToNextStep(WorkflowInstance instance) {
        WorkflowStep nextStep = workflowStepRepository
            .findNextStep(instance.getWorkflowDefinitionId(),
                         instance.getCurrentStepId())
            .orElse(null);

        if (nextStep == null) {
            // Workflow complete
            completeWorkflow(instance);
        } else {
            // Move to next step
            instance.setCurrentStepId(nextStep.getId());
            workflowInstanceRepository.save(instance);

            logWorkflowHistory(instance.getId(), "STEP_ADVANCED", null,
                instance.getStatus().toString(), instance.getStatus().toString(),
                nextStep.getStepName(), "Advanced to next step");

            processCurrentStep(instance);
        }
    }

    /**
     * Complete workflow successfully
     */
    private void completeWorkflow(WorkflowInstance instance) {
        instance.setStatus(WorkflowStatus.APPROVED);
        instance.setCompletedAt(LocalDateTime.now());
        workflowInstanceRepository.save(instance);

        logWorkflowHistory(instance.getId(), "WORKFLOW_COMPLETED",
                null, WorkflowStatus.PENDING.toString(),
                WorkflowStatus.APPROVED.toString(), null, "Workflow completed successfully");

    }

    /**
     * Reject workflow
     */
    private void rejectWorkflow(WorkflowInstance instance, String reason) {
        instance.setStatus(WorkflowStatus.REJECTED);
        instance.setCompletedAt(LocalDateTime.now());
        workflowInstanceRepository.save(instance);

        logWorkflowHistory(instance.getId(), "WORKFLOW_REJECTED", null,
            WorkflowStatus.PENDING.toString(),
            WorkflowStatus.REJECTED.toString(),
            null, "Workflow rejected: " + reason);

        // Publish workflow rejected event
        eventPublisher.publishEvent(new WorkflowRejectedEvent(instance, reason));
    }

    /**
     * Cancel workflow
     */
    public void cancelWorkflow(Long instanceId, Long cancelledBy, String reason) {
        WorkflowInstance instance = workflowInstanceRepository
            .findById(instanceId)
            .orElseThrow(() -> new WorkflowInstanceNotFoundException("Instance not found"));

        if (instance.getStatus() != WorkflowStatus.PENDING) {
            throw new InvalidWorkflowStateException("Cannot cancel workflow in status: " + instance.getStatus());
        }

        instance.setStatus(WorkflowStatus.CANCELLED);
        instance.setCompletedAt(LocalDateTime.now());
        workflowInstanceRepository.save(instance);

        logWorkflowHistory(instance.getId(), "WORKFLOW_CANCELLED", cancelledBy,
            WorkflowStatus.PENDING.toString(),
            WorkflowStatus.CANCELLED.toString(),
            null, "Workflow cancelled: " + reason);

        // Publish workflow cancelled event
        eventPublisher.publishEvent(new WorkflowCancelledEvent(instance, reason));
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    private void validateWorkflowRequest(InitiateWorkflowRequest request) {
        if (request.getEntityType() == null || request.getEntityType().trim().isEmpty()) {
            throw new IllegalArgumentException("Entity type is required");
        }
        if (request.getEntityId() == null) {
            throw new IllegalArgumentException("Entity ID is required");
        }
        if (request.getInitiatedBy() == null) {
            throw new IllegalArgumentException("Initiated by user ID is required");
        }
    }

    private void checkExistingWorkflow(Long entityId, String entityType) {
        List<WorkflowInstance> existingInstance = workflowInstanceRepository
            .findByEntityIdAndEntityTypeAndStatus(entityId, entityType, WorkflowStatus.PENDING);

        if (!existingInstance.isEmpty()) {
            return;
        }
        throw new WorkflowAlreadyExistsException(
                STR."Workflow already in progress for entity: \{entityType} ID: \{entityId}");
    }

    private WorkflowInstance createWorkflowInstance(InitiateWorkflowRequest request, WorkflowDefinition workflow) {
        WorkflowInstance instance = new WorkflowInstance();
        instance.setWorkflowDefinitionId(workflow.getId());
        instance.setEntityId(request.getEntityId());
        instance.setEntityType(request.getEntityType());
        instance.setStatus(WorkflowStatus.PENDING);
        instance.setPriority(request.getPriority() != null ? request.getPriority() : WorkflowPriority.MEDIUM);
        instance.setInitiatedBy(request.getInitiatedBy());
        instance.setMetadata(request.getMetadata());
        return instance;
    }

    private WorkflowStep getFirstWorkflowStep(Long workflowDefinitionId) {
        return workflowStepRepository
            .findFirstStepByWorkflowDefinitionId(workflowDefinitionId)
            .orElseThrow(() -> new WorkflowConfigurationException(
                "No steps configured for workflow"));
    }

    private void validateApprovalRequest(WorkflowApproval approval, ProcessApprovalRequest request) {
        if (approval.getStatus() != ApprovalStatus.PENDING) {
            throw new InvalidApprovalStateException("Approval already processed with status: " + approval.getStatus());
        }

        if (!approval.getApproverId().equals(request.getApproverId())) {
            throw new UnauthorizedApprovalException("User not authorized to process this approval");
        }

        if (request.getDecision() == null) {
            throw new IllegalArgumentException("Approval decision is required");
        }
    }

    private boolean checkStepCompletion(Long stepId, Long instanceId) {
        WorkflowStep step = workflowStepRepository.findById(stepId)
                .orElseThrow(() -> new WorkflowStepNotFoundException("Step not found"));

        List<WorkflowApproval> approvals = workflowApprovalRepository
                .findByWorkflowInstanceIdAndWorkflowStepIdNative(instanceId, stepId);

        if (approvals.isEmpty()) {
            return false;
        }

        return switch (step.getApprovalType()) {
            case SINGLE -> approvals.stream()
                    .anyMatch(a -> a.getStatus() != ApprovalStatus.PENDING);
            case ALL -> approvals.stream()
                    .allMatch(a -> a.getStatus() != ApprovalStatus.PENDING);
            case MAJORITY -> {
                long processedCount = approvals.stream()
                        .filter(a -> a.getStatus() != ApprovalStatus.PENDING)
                        .count();
                yield processedCount > (approvals.size() / 2);
            }
            default -> false;
        };
    }


    private boolean isStepApproved(Long stepId, Long instanceId) {
        WorkflowStep step = workflowStepRepository.findById(stepId)
            .orElseThrow(() -> new WorkflowStepNotFoundException("Step not found"));

        List<WorkflowApproval> approvals = workflowApprovalRepository
            .findByWorkflowInstanceIdAndWorkflowStepIdNative(instanceId, stepId);

        // If any approval is rejected, step is rejected
        if (approvals.stream().anyMatch(a -> a.getStatus() == ApprovalStatus.REJECTED)) {
            return false;
        }

        return switch (step.getApprovalType()) {
            case SINGLE -> approvals.stream()
                    .anyMatch(a -> a.getStatus() == ApprovalStatus.APPROVED);
            case ALL -> approvals.stream()
                    .allMatch(a -> a.getStatus() == ApprovalStatus.APPROVED);
            case MAJORITY -> {
                long approvedCount = approvals.stream()
                        .filter(a -> a.getStatus() == ApprovalStatus.APPROVED)
                        .count();
                yield approvedCount > (approvals.size() / 2);
            }
            default -> false;
        };
    }

    private void scheduleEscalation(Long instanceId, Long stepId, Integer timeoutHours) {
        // This would integrate with a job scheduler like Quartz
        // For now, we'll publish an event that can be handled by a scheduler
        eventPublisher.publishEvent(new WorkflowEscalationScheduledEvent(instanceId, stepId, timeoutHours));
    }

    private String getStepName(Long stepId) {
        return workflowStepRepository.findById(stepId)
            .map(WorkflowStep::getStepName)
            .orElse("Unknown Step");
    }

    private void logWorkflowHistory(Long instanceId, String action, Long actorId,
                                  String fromStatus, String toStatus, String stepName, String comments) {
        WorkflowHistory history = new WorkflowHistory();
        history.setWorkflowInstanceId(instanceId);
        history.setAction(action);
        history.setActorId(actorId);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setStepName(stepName);
        history.setComments(comments);
        workflowHistoryRepository.save(history);
    }

    private WorkflowInstanceDTO convertToDTO(WorkflowInstance instance) {
        WorkflowInstanceDTO dto = new WorkflowInstanceDTO();
        dto.setId(instance.getId());
        dto.setWorkflowDefinitionId(instance.getWorkflowDefinitionId());
        dto.setEntityId(instance.getEntityId());
        dto.setEntityType(instance.getEntityType());
        dto.setCurrentStepId(instance.getCurrentStepId());
        dto.setStatus(instance.getStatus());
        dto.setPriority(instance.getPriority());
        dto.setInitiatedBy(instance.getInitiatedBy());
        dto.setInitiatedAt(instance.getInitiatedAt());
        dto.setCompletedAt(instance.getCompletedAt());
        dto.setMetadata(instance.getMetadata());
        return dto;
    }
}