package com.mohit.workflow.dto;

import com.mohit.workflow.entity.WorkflowPriority;
import lombok.*;

import java.util.Map;
@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InitiateWorkflowRequest {
    private String entityType;
    private Long entityId;
    private Long initiatedBy;
    private WorkflowPriority priority;
    private Map<String, Object> metadata;

}