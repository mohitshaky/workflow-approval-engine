package com.mohit.workflow.dto;

import com.mohit.workflow.entity.ApprovalStatus;
import com.mohit.workflow.entity.WorkflowPriority;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

public interface PendingApprovalDTO {
    Long getId();
    Long getWorkflowInstanceId();
    Long getWorkflowDefinitionId();
    Long getEntityId();
    String getEntityType();
    String getCurrentStepName();
    WorkflowPriority getPriority();
    LocalDateTime getCreatedAt();
    Long getHoursWaiting();
    Map<String, Object> getMetadata();
    ApprovalStatus getStatus();
}