package com.mohit.workflow.dto;

import lombok.*;

@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateApprovalRuleRequest {
    private Long workflowDefinitionId;
    private String ruleName;
    private String entityField;
    private String operator;
    private String thresholdValue;
    private String requiredApproverRole;
    private Integer approvalLevel;

    public Long getWorkflowDefinitionId() { return workflowDefinitionId; }
    public void setWorkflowDefinitionId(Long workflowDefinitionId) { this.workflowDefinitionId = workflowDefinitionId; }
    
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    
    public String getEntityField() { return entityField; }
    public void setEntityField(String entityField) { this.entityField = entityField; }
    
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    
    public String getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(String thresholdValue) { this.thresholdValue = thresholdValue; }
    
    public String getRequiredApproverRole() { return requiredApproverRole; }
    public void setRequiredApproverRole(String requiredApproverRole) { this.requiredApproverRole = requiredApproverRole; }
    
    public Integer getApprovalLevel() { return approvalLevel; }
    public void setApprovalLevel(Integer approvalLevel) { this.approvalLevel = approvalLevel; }
}