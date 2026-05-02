package com.mohit.workflow.exception;

public class InvalidApprovalStateException extends RuntimeException {
    public InvalidApprovalStateException(String message) { super(message); }
}