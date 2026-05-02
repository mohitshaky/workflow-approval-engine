package com.mohit.workflow.events;

import lombok.Getter;

// Workflow Escalation Scheduled Event
@Getter
public class WorkflowEscalationScheduledEvent extends WorkflowEvent {
    private final Long stepId;
    private final Integer timeoutHours;
    
    public WorkflowEscalationScheduledEvent(Long instanceId, Long stepId, Integer timeoutHours) {
        super(stepId, instanceId, "ESCALATION");
        this.stepId = stepId;
        this.timeoutHours = timeoutHours;
    }

}