package com.mohit.workflow.dto;

import com.mohit.workflow.entity.WorkflowPriority;
import com.mohit.workflow.entity.WorkflowStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Setter
@Getter
public class WorkflowInstanceDTO {
    // All getters and setters
    private Long id;
    private Long workflowDefinitionId;
    private Long entityId;
    private String entityType;
    private Long currentStepId;
    private WorkflowStatus status;
    private WorkflowPriority priority;
    private Long initiatedBy;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private Map<String, Object> metadata;
    
    // Constructors, getters, setters
    public WorkflowInstanceDTO() {}

}