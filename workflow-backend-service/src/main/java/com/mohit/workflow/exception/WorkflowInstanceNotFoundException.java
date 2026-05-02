package com.mohit.workflow.exception;

public class WorkflowInstanceNotFoundException extends RuntimeException {
    public WorkflowInstanceNotFoundException(String message) { super(message); }
}