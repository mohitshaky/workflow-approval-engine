package com.mohit.workflow.events;

import com.mohit.workflow.entity.WorkflowApproval;
import com.mohit.workflow.entity.WorkflowInstance;
import com.mohit.workflow.entity.WorkflowStep;
import lombok.Getter;

import java.util.List;

@Getter
public class ApprovalStepStartedEvent extends WorkflowEvent {
    private final WorkflowInstance workflowInstance;
    private final WorkflowStep workflowStep;
    private final List<WorkflowApproval> approvals;
    
    public ApprovalStepStartedEvent(WorkflowInstance workflowInstance,
                                    WorkflowStep workflowStep,
                                    List<WorkflowApproval> approvals) {
        super(workflowInstance, workflowInstance.getId(), workflowInstance.getEntityType());
        this.workflowInstance = workflowInstance;
        this.workflowStep = workflowStep;
        this.approvals = approvals;
    }

}