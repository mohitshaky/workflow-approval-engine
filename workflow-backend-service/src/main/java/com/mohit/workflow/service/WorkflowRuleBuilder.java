//package com.mohit.workflow.service;
//
//import com.mohit.workflow.dto.CreateApprovalRuleRequest;
//import com.mohit.workflow.dto.RuleTestResult;
//import com.mohit.workflow.dto.TestRuleRequest;
//import com.mohit.workflow.dto.UpdateApprovalRuleRequest;
//import com.mohit.workflow.entity.ApprovalRule;
//import com.mohit.workflow.entity.WorkflowInstance;
//import com.mohit.workflow.exception.ApprovalRuleNotFoundException;
//import com.mohit.workflow.repository.ApprovalRuleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class WorkflowRuleBuilder {
//
//    @Autowired
//    private ApprovalRuleRepository approvalRuleRepository;
//
//    /**
//     * Create a new approval rule
//     */
//    public ApprovalRule createRule(CreateApprovalRuleRequest request) {
//        validateRuleRequest(request);
//
//        ApprovalRule rule = new ApprovalRule();
//        rule.setWorkflowDefinitionId(request.getWorkflowDefinitionId());
//        rule.setRuleName(request.getRuleName());
//        rule.setEntityField(request.getEntityField());
//        rule.setOperator(request.getOperator());
//        rule.setThresholdValue(request.getThresholdValue());
//        rule.setRequiredApproverRole(request.getRequiredApproverRole());
//        rule.setApprovalLevel(request.getApprovalLevel());
//        rule.setIsActive(true);
//
//        return approvalRuleRepository.save(rule);
//    }
//
//    /**
//     * Update an existing approval rule
//     */
//    public ApprovalRule updateRule(Long ruleId, UpdateApprovalRuleRequest request) {
//        ApprovalRule rule = approvalRuleRepository.findById(ruleId)
//            .orElseThrow(() -> new ApprovalRuleNotFoundException("Rule not found"));
//
//        if (request.getRuleName() != null) {
//            rule.setRuleName(request.getRuleName());
//        }
//        if (request.getEntityField() != null) {
//            rule.setEntityField(request.getEntityField());
//        }
//        if (request.getOperator() != null) {
//            rule.setOperator(request.getOperator());
//        }
//        if (request.getThresholdValue() != null) {
//            rule.setThresholdValue(request.getThresholdValue());
//        }
//        if (request.getRequiredApproverRole() != null) {
//            rule.setRequiredApproverRole(request.getRequiredApproverRole());
//        }
//        if (request.getApprovalLevel() != null) {
//            rule.setApprovalLevel(request.getApprovalLevel());
//        }
//        if (request.getIsActive() != null) {
//            rule.setIsActive(request.getIsActive());
//        }
//
//        return approvalRuleRepository.save(rule);
//    }
//
//    /**
//     * Delete (deactivate) an approval rule
//     */
//    public void deleteRule(Long ruleId) {
//        ApprovalRule rule = approvalRuleRepository.findById(ruleId)
//            .orElseThrow(() -> new ApprovalRuleNotFoundException("Rule not found"));
//
//        rule.setIsActive(false);
//        approvalRuleRepository.save(rule);
//    }
//
//    /**
//     * Test a rule against sample data
//     */
//    public RuleTestResult testRule(TestRuleRequest request) {
//        // Create a temporary rule for testing
//        ApprovalRule rule = new ApprovalRule();
//        rule.setEntityField(request.getEntityField());
//        rule.setOperator(request.getOperator());
//        rule.setThresholdValue(request.getThresholdValue());
//
//        // Create mock workflow instance with test data
//        WorkflowInstance mockInstance = new WorkflowInstance();
//        mockInstance.setMetadata(request.getTestData());
//
//        WorkflowRuleEngine ruleEngine = new WorkflowRuleEngine();
//        boolean result = ruleEngine.evaluateRule(rule, mockInstance);
//
//        return new RuleTestResult(result, "Rule evaluation completed");
//    }
//
//    private void validateRuleRequest(CreateApprovalRuleRequest request) {
//        if (request.getWorkflowDefinitionId() == null) {
//            throw new IllegalArgumentException("Workflow definition ID is required");
//        }
//        if (request.getRuleName() == null || request.getRuleName().trim().isEmpty()) {
//            throw new IllegalArgumentException("Rule name is required");
//        }
//        if (request.getOperator() == null) {
//            throw new IllegalArgumentException("Operator is required");
//        }
//        if (request.getRequiredApproverRole() == null || request.getRequiredApproverRole().trim().isEmpty()) {
//            throw new IllegalArgumentException("Required approver role is required");
//        }
//    }
//}
