package com.mohit.workflow.controller;

import com.mohit.workflow.dto.*;
import com.mohit.workflow.service.WorkflowEngine;
import com.mohit.workflow.service.WorkflowQueryService;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RestControllerAdvice
@RequestMapping("/api/workflows")
//@CrossOrigin(origins = "*")
public class WorkflowController {
    
    @Autowired
    private WorkflowEngine workflowEngine;
    
    @Autowired
    private WorkflowQueryService workflowQueryService;


    
    /**
     * Initiate a new workflow
     */
    @PostMapping("/initiate")
//    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'KYC_OFFICER', 'FINANCE_MANAGER')")
    public ResponseEntity<ApiResponse<WorkflowInstanceDTO>> initiateWorkflow(
            @Valid @RequestBody InitiateWorkflowRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) throws JsonProcessingException {

        WorkflowInstanceDTO instance = workflowEngine.initiateWorkflow(request);

        return ResponseEntity.ok(new ApiResponse<>(
            "Workflow initiated successfully",
            instance,
            "WORKFLOW_INITIATED"));
    }
    
    /**
     * Process approval decision
     */
    @PostMapping("/approvals/{approvalId}/process")
//    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'KYC_OFFICER', 'FINANCE_MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> processApproval(
            @PathVariable Long approvalId,
            @Valid @RequestBody ProcessApprovalRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        request.setApprovalId(approvalId);
        workflowEngine.processApproval(request);
        
        return ResponseEntity.ok(new ApiResponse<>(
            "Approval processed successfully", 
            null, 
            "APPROVAL_PROCESSED"));
    }
    
    /**
     * Get pending approvals for current user
     */
    @GetMapping("/approvals/pending")
    public ResponseEntity<ApiResponse<PagedResponse<PendingApprovalDTO>>> getPendingApprovals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        if(userId==null) {
            userId = extractUserIdFromToken(token);
        }
        Pageable pageable = PageRequest.of(page, size);

        Page<PendingApprovalDTO> approvals = workflowQueryService
                .getPendingApprovalsForUser(userId, priority, entityType, pageable);

        PagedResponse<PendingApprovalDTO> response = new PagedResponse<>(
                approvals.getContent(),
                approvals.getTotalElements()
        );

        return ResponseEntity.ok(new ApiResponse<>(
                "Pending approvals retrieved successfully",
                response,
                "APPROVALS_RETRIEVED"));
    }


    
    /**
     * Get pending approvals count for current user
     */
    @GetMapping("/approvals/pending/count")
    public ResponseEntity<ApiResponse<ApprovalCountDTO>> getPendingApprovalsCount(
            @RequestParam(required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        if(userId==null) {
            userId = extractUserIdFromToken(token);
        }
        ApprovalCountDTO count = workflowQueryService.getPendingApprovalsCount(userId);
        
        return ResponseEntity.ok(new ApiResponse<>(
            "Approval count retrieved successfully", 
            count, 
            "APPROVAL_COUNT_RETRIEVED"));
    }
    
    /**
     * Get workflow instance details
     */
    @GetMapping("/instances/{instanceId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'KYC_OFFICER', 'FINANCE_MANAGER')")
    public ResponseEntity<ApiResponse<WorkflowInstanceDetailDTO>> getWorkflowInstance(
            @PathVariable Long instanceId,
    @RequestHeader(value = "Authorization", required = false) String token) {


        WorkflowInstanceDetailDTO instance = workflowQueryService
            .getWorkflowInstanceDetails(instanceId);
        
        return ResponseEntity.ok(new ApiResponse<>(
            "Workflow instance retrieved successfully", 
            instance, 
            "INSTANCE_RETRIEVED"));
    }
    
    /**
     * Get workflow history
     */
    @GetMapping("/instances/{instanceId}/history")
//    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'KYC_OFFICER', 'FINANCE_MANAGER')")
    public ResponseEntity<ApiResponse<List<WorkflowHistoryDTO>>> getWorkflowHistory(
            @PathVariable Long instanceId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        List<WorkflowHistoryDTO> history = workflowQueryService
            .getWorkflowHistory(instanceId);
        
        return ResponseEntity.ok(new ApiResponse<>(
            "Workflow history retrieved successfully", 
            history, 
            "HISTORY_RETRIEVED"));
    }
    

    /**
     * Get workflow analytics
     */
    @GetMapping("/analytics")
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<WorkflowAnalyticsDTO>> getWorkflowAnalytics(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        WorkflowAnalyticsDTO analytics = workflowQueryService
            .getWorkflowAnalytics(entityType, fromDate, toDate);
        
        return ResponseEntity.ok(new ApiResponse<>(
            "Workflow analytics retrieved successfully", 
            analytics, 
            "ANALYTICS_RETRIEVED"));
    }
    

    private Long extractUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) return null;

        String jwt = token.substring(7); // Remove "Bearer "
        try {
            // Decode JWT payload (base64)
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) return null;
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));

            // Parse JSON to extract "userId"
            com.fasterxml.jackson.databind.JsonNode node =
                    new com.fasterxml.jackson.databind.ObjectMapper().readTree(payload);
            return node.has("userId") ? node.get("userId").asLong() : null;

        } catch (Exception e) {
            return null;
        }
    }
}



