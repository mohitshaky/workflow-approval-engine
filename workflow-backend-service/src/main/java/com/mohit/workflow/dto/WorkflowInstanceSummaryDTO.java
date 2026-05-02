package com.mohit.workflow.dto;

import com.mohit.workflow.entity.WorkflowPriority;
import com.mohit.workflow.entity.WorkflowStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowInstanceSummaryDTO {
    private Long id;
    private String workflowName;
    private String entityType;
    private Long entityId;
    private WorkflowStatus status;
    private WorkflowPriority priority;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private String currentStepName;
    
    // Constructors, getters, setters
}