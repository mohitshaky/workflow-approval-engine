package com.mohit.workflow.events;

import com.mohit.workflow.entity.WorkflowApproval;
import com.mohit.workflow.entity.WorkflowInstance;
import lombok.Getter;

@Getter
public class ApprovalProcessedEvent extends WorkflowEvent {
    private final WorkflowApproval approval;
    private final WorkflowInstance workflowInstance;
    
    public ApprovalProcessedEvent(WorkflowApproval approval, WorkflowInstance workflowInstance) {
        super(approval, workflowInstance.getId(), workflowInstance.getEntityType());
        this.approval = approval;
        this.workflowInstance = workflowInstance;
    }

}