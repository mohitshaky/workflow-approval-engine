package com.mohit.workflow.dto;

import com.mohit.workflow.entity.WorkflowPriority;
import com.mohit.workflow.entity.WorkflowStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowInstanceDetailDTO {
    private Long id;
    private String workflowName;
    private String entityType;
    private Long entityId;
    private WorkflowStatus status;
    private WorkflowPriority priority;
    private String initiatedByName;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private String currentStepName;
    private Map<String, Object> metadata;

    // Constructors, getters, setters
}