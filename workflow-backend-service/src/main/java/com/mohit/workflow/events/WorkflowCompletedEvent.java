package com.mohit.workflow.events;

import com.mohit.workflow.entity.WorkflowInstance;
import lombok.Getter;

@Getter
public class WorkflowCompletedEvent extends WorkflowEvent {
    private final WorkflowInstance workflowInstance;
    
    public WorkflowCompletedEvent(WorkflowInstance workflowInstance) {
        super(workflowInstance, workflowInstance.getId(), workflowInstance.getEntityType());
        this.workflowInstance = workflowInstance;
    }

}