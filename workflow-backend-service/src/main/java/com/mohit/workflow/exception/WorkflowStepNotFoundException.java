package com.mohit.workflow.exception;

public class WorkflowStepNotFoundException extends RuntimeException {
    public WorkflowStepNotFoundException(String message) { super(message); }
}