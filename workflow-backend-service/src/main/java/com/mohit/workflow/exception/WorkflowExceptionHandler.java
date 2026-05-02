package com.mohit.workflow.exception;

import com.mohit.workflow.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WorkflowExceptionHandler {
    
    @ExceptionHandler(WorkflowNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleWorkflowNotFound(WorkflowNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse<>(ex.getMessage(), null, "WORKFLOW_NOT_FOUND"));
    }
    
    @ExceptionHandler(WorkflowAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleWorkflowAlreadyExists(WorkflowAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiResponse<>(ex.getMessage(), null, "WORKFLOW_ALREADY_EXISTS"));
    }
    
    @ExceptionHandler(InvalidApprovalStateException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidApprovalState(InvalidApprovalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(ex.getMessage(), null, "INVALID_APPROVAL_STATE"));
    }
    
    @ExceptionHandler(UnauthorizedApprovalException.class)
    public ResponseEntity<ApiResponse<String>> handleUnauthorizedApproval(UnauthorizedApprovalException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ApiResponse<>(ex.getMessage(), null, "UNAUTHORIZED_APPROVAL"));
    }
    
    @ExceptionHandler(WorkflowConfigurationException.class)
    public ResponseEntity<ApiResponse<String>> handleWorkflowConfiguration(WorkflowConfigurationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApiResponse<>(ex.getMessage(), null, "WORKFLOW_CONFIGURATION_ERROR"));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse<>("An unexpected error occurred", null, "INTERNAL_SERVER_ERROR"));
    }
}