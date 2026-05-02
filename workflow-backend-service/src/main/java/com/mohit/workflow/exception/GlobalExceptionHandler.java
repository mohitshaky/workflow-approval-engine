package com.mohit.workflow.exception;

import com.mohit.workflow.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle ApprovalNotFoundException
    @ExceptionHandler(ApprovalNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleApprovalNotFound(ApprovalNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), null, "APPROVAL_NOT_FOUND"));
    }

    // Handle InvalidApprovalStateException
    @ExceptionHandler(InvalidApprovalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidApprovalState(InvalidApprovalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(ex.getMessage(), null, "INVALID_APPROVAL_STATE"));
    }

    // Handle WorkflowAlreadyExistsException
    @ExceptionHandler(WorkflowAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleWorkflowAlreadyExists(WorkflowAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(ex.getMessage(), null, "WORKFLOW_ALREADY_EXISTS"));
    }

    // Handle WorkflowNotFoundException
    @ExceptionHandler(WorkflowNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleWorkflowNotFound(WorkflowNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), null, "WORKFLOW_NOT_FOUND"));
    }
    @ExceptionHandler(WorkflowInstanceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleWorkflowInstanceNotFound(WorkflowInstanceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), null, "WORKFLOW_INSTANCE_NOT_FOUND"));
    }

    // Handle WorkflowConfigurationException
    @ExceptionHandler(WorkflowConfigurationException.class)
    public ResponseEntity<ApiResponse<Void>> handleWorkflowConfiguration(WorkflowConfigurationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(ex.getMessage(), null, "WORKFLOW_CONFIGURATION_ERROR"));
    }

    // Generic handler for any other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Something went wrong: " + ex.getMessage(), null, "INTERNAL_ERROR"));
    }
}
