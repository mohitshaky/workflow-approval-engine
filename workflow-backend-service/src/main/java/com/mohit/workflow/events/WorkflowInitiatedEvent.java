package com.mohit.workflow.events;

import com.mohit.workflow.entity.WorkflowInstance;
import lombok.Getter;

@Getter
public class WorkflowInitiatedEvent extends WorkflowEvent {
    private final WorkflowInstance workflowInstance;
    
    public WorkflowInitiatedEvent(WorkflowInstance workflowInstance) {
        super(workflowInstance, workflowInstance.getId(), workflowInstance.getEntityType());
        this.workflowInstance = workflowInstance;
    }

}