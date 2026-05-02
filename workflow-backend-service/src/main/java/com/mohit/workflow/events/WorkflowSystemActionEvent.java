package com.mohit.workflow.events;

import com.mohit.workflow.entity.WorkflowInstance;
import com.mohit.workflow.entity.WorkflowStep;
import lombok.Getter;

@Getter
public class WorkflowSystemActionEvent extends WorkflowEvent {
    private final WorkflowInstance workflowInstance;
    private final WorkflowStep workflowStep;
    
    public WorkflowSystemActionEvent(WorkflowInstance workflowInstance, WorkflowStep workflowStep) {
        super(workflowInstance, workflowInstance.getId(), workflowInstance.getEntityType());
        this.workflowInstance = workflowInstance;
        this.workflowStep = workflowStep;
    }

}