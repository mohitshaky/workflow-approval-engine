package com.mohit.workflow.events;

import com.mohit.workflow.entity.WorkflowInstance;
import lombok.Getter;

@Getter
public class WorkflowCancelledEvent extends WorkflowEvent {
    private final WorkflowInstance workflowInstance;
    private final String reason;
    
    public WorkflowCancelledEvent(WorkflowInstance workflowInstance, String reason) {
        super(workflowInstance, workflowInstance.getId(), workflowInstance.getEntityType());
        this.workflowInstance = workflowInstance;
        this.reason = reason;
    }

}