//package com.mohit.workflow.controller;
//
//import com.mohit.workflow.dto.*;
//import com.mohit.workflow.entity.ApprovalRule;
//import com.mohit.workflow.service.WorkflowRuleBuilder;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/admin/workflows")
//@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//@CrossOrigin(origins = "*")
//public class WorkflowAdminController {
//
//    @Autowired
//    private WorkflowAdminService workflowAdminService;
//
//    @Autowired
//    private WorkflowRuleBuilder workflowRuleBuilder;
//
//    @Autowired
//    private UserPrincipalService userPrincipalService;
//
//    /**
//     * Get all workflow definitions
//     */
//    @GetMapping("/definitions")
//    public ResponseEntity<ApiResponse<List<WorkflowDefinitionDTO>>> getWorkflowDefinitions(
//            @RequestParam(required = false) Boolean isActive) {
//
//        List<WorkflowDefinitionDTO> definitions = workflowAdminService
//            .getWorkflowDefinitions(isActive);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Workflow definitions retrieved successfully",
//            definitions,
//            "DEFINITIONS_RETRIEVED"));
//    }
//
//    /**
//     * Create workflow definition
//     */
//    @PostMapping("/definitions")
//    public ResponseEntity<ApiResponse<WorkflowDefinitionDTO>> createWorkflowDefinition(
//            @Valid @RequestBody CreateWorkflowDefinitionRequest request,
//            Authentication authentication) {
//
//        request.setCreatedBy(getCurrentUserId(authentication));
//        WorkflowDefinitionDTO definition = workflowAdminService
//            .createWorkflowDefinition(request);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//            .body(new ApiResponse<>("Workflow definition created successfully",
//                                  definition,
//                                  "DEFINITION_CREATED"));
//    }
//
//    /**
//     * Update workflow definition
//     */
//    @PutMapping("/definitions/{definitionId}")
//    public ResponseEntity<ApiResponse<WorkflowDefinitionDTO>> updateWorkflowDefinition(
//            @PathVariable Long definitionId,
//            @Valid @RequestBody UpdateWorkflowDefinitionRequest request) {
//
//        WorkflowDefinitionDTO definition = workflowAdminService
//            .updateWorkflowDefinition(definitionId, request);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Workflow definition updated successfully",
//            definition,
//            "DEFINITION_UPDATED"));
//    }
//
//    /**
//     * Get workflow steps for a definition
//     */
//    @GetMapping("/definitions/{definitionId}/steps")
//    public ResponseEntity<ApiResponse<List<WorkflowStepDTO>>> getWorkflowSteps(
//            @PathVariable Long definitionId) {
//
//        List<WorkflowStepDTO> steps = workflowAdminService
//            .getWorkflowSteps(definitionId);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Workflow steps retrieved successfully",
//            steps,
//            "STEPS_RETRIEVED"));
//    }
//
//    /**
//     * Add workflow step
//     */
//    @PostMapping("/definitions/{definitionId}/steps")
//    public ResponseEntity<ApiResponse<WorkflowStepDTO>> addWorkflowStep(
//            @PathVariable Long definitionId,
//            @Valid @RequestBody CreateWorkflowStepRequest request) {
//
//        request.setWorkflowDefinitionId(definitionId);
//        WorkflowStepDTO step = workflowAdminService.addWorkflowStep(request);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//            .body(new ApiResponse<>("Workflow step added successfully",
//                                  step,
//                                  "STEP_CREATED"));
//    }
//
//    /**
//     * Update workflow step
//     */
//    @PutMapping("/steps/{stepId}")
//    public ResponseEntity<ApiResponse<WorkflowStepDTO>> updateWorkflowStep(
//            @PathVariable Long stepId,
//            @Valid @RequestBody UpdateWorkflowStepRequest request) {
//
//        WorkflowStepDTO step = workflowAdminService
//            .updateWorkflowStep(stepId, request);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Workflow step updated successfully",
//            step,
//            "STEP_UPDATED"));
//    }
//
//    /**
//     * Delete workflow step
//     */
//    @DeleteMapping("/steps/{stepId}")
//    public ResponseEntity<ApiResponse<Void>> deleteWorkflowStep(
//            @PathVariable Long stepId) {
//
//        workflowAdminService.deleteWorkflowStep(stepId);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Workflow step deleted successfully",
//            null,
//            "STEP_DELETED"));
//    }
//
//    /**
//     * Get approval rules for a workflow
//     */
//    @GetMapping("/definitions/{definitionId}/rules")
//    public ResponseEntity<ApiResponse<List<ApprovalRuleDTO>>> getApprovalRules(
//            @PathVariable Long definitionId) {
//
//        List<ApprovalRuleDTO> rules = workflowAdminService
//            .getApprovalRules(definitionId);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Approval rules retrieved successfully",
//            rules,
//            "RULES_RETRIEVED"));
//    }
//
//    /**
//     * Add approval rule
//     */
//    @PostMapping("/definitions/{definitionId}/rules")
//    public ResponseEntity<ApiResponse<ApprovalRuleDTO>> addApprovalRule(
//            @PathVariable Long definitionId,
//            @Valid @RequestBody CreateApprovalRuleRequest request) {
//
//        request.setWorkflowDefinitionId(definitionId);
//        ApprovalRule rule = workflowRuleBuilder.createRule(request);
//        ApprovalRuleDTO ruleDTO = convertToDTO(rule);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//            .body(new ApiResponse<>("Approval rule added successfully",
//                                  ruleDTO,
//                                  "RULE_CREATED"));
//    }
//
//    /**
//     * Update approval rule
//     */
//    @PutMapping("/rules/{ruleId}")
//    public ResponseEntity<ApiResponse<ApprovalRuleDTO>> updateApprovalRule(
//            @PathVariable Long ruleId,
//            @Valid @RequestBody UpdateApprovalRuleRequest request) {
//
//        ApprovalRule rule = workflowRuleBuilder.updateRule(ruleId, request);
//        ApprovalRuleDTO ruleDTO = convertToDTO(rule);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Approval rule updated successfully",
//            ruleDTO,
//            "RULE_UPDATED"));
//    }
//
//    /**
//     * Delete approval rule
//     */
//    @DeleteMapping("/rules/{ruleId}")
//    public ResponseEntity<ApiResponse<Void>> deleteApprovalRule(
//            @PathVariable Long ruleId) {
//
//        workflowRuleBuilder.deleteRule(ruleId);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Approval rule deleted successfully",
//            null,
//            "RULE_DELETED"));
//    }
//
//    /**
//     * Test approval rule
//     */
//    @PostMapping("/rules/test")
//    public ResponseEntity<ApiResponse<RuleTestResult>> testApprovalRule(
//            @Valid @RequestBody TestRuleRequest request) {
//
//        RuleTestResult result = workflowRuleBuilder.testRule(request);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Rule test completed",
//            result,
//            "RULE_TESTED"));
//    }
//
//    /**
//     * Assign workflow role to user
//     */
//    @PostMapping("/roles/assign")
//    public ResponseEntity<ApiResponse<Void>> assignWorkflowRole(
//            @Valid @RequestBody AssignWorkflowRoleRequest request,
//            Authentication authentication) {
//
//        request.setAssignedBy(getCurrentUserId(authentication));
//        workflowAdminService.assignWorkflowRole(request);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Workflow role assigned successfully",
//            null,
//            "ROLE_ASSIGNED"));
//    }
//
//    /**
//     * Remove workflow role from user
//     */
//    @PostMapping("/roles/remove")
//    public ResponseEntity<ApiResponse<Void>> removeWorkflowRole(
//            @Valid @RequestBody RemoveWorkflowRoleRequest request) {
//
//        workflowAdminService.removeWorkflowRole(request);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Workflow role removed successfully",
//            null,
//            "ROLE_REMOVED"));
//    }
//
//    /**
//     * Get user workflow roles
//     */
//    @GetMapping("/users/{userId}/roles")
//    public ResponseEntity<ApiResponse<List<UserWorkflowRoleDTO>>> getUserWorkflowRoles(
//            @PathVariable Long userId) {
//
//        List<UserWorkflowRoleDTO> roles = workflowAdminService
//            .getUserWorkflowRoles(userId);
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "User workflow roles retrieved successfully",
//            roles,
//            "USER_ROLES_RETRIEVED"));
//    }
//
//    /**
//     * Get all available workflow roles
//     */
//    @GetMapping("/roles/available")
//    public ResponseEntity<ApiResponse<List<WorkflowRoleDefinitionDTO>>> getAvailableWorkflowRoles() {
//
//        List<WorkflowRoleDefinitionDTO> roles = workflowAdminService
//            .getAvailableWorkflowRoles();
//
//        return ResponseEntity.ok(new ApiResponse<>(
//            "Available workflow roles retrieved successfully",
//            roles,
//            "AVAILABLE_ROLES_RETRIEVED"));
//    }
//
//    private Long getCurrentUserId(Authentication authentication) {
//        return userPrincipalService.getCurrentUserId(authentication);
//    }
//
//    private ApprovalRuleDTO convertToDTO(ApprovalRule rule) {
//        ApprovalRuleDTO dto = new ApprovalRuleDTO();
//        dto.setId(rule.getId());
//        dto.setWorkflowDefinitionId(rule.getWorkflowDefinitionId());
//        dto.setRuleName(rule.getRuleName());
//        dto.setEntityField(rule.getEntityField());
//        dto.setOperator(rule.getOperator());
//        dto.setThresholdValue(rule.getThresholdValue());
//        dto.setRequiredApproverRole(rule.getRequiredApproverRole());
//        dto.setApprovalLevel(rule.getApprovalLevel());
//        dto.setIsActive(rule.getIsActive());
//        dto.setCreatedAt(rule.getCreatedAt());
//        return dto;
//    }
//}