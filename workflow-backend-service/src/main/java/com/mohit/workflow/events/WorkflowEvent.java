package com.mohit.workflow.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

abstract class WorkflowEvent extends ApplicationEvent {
    @Getter
    private final Long workflowInstanceId;
    @Getter
    private final String entityType;

    public WorkflowEvent(Object source, Long workflowInstanceId, String entityType) {
        super(source);
        this.workflowInstanceId = workflowInstanceId;
        this.entityType = entityType;
        LocalDateTime timestamp = LocalDateTime.now();
    }

}