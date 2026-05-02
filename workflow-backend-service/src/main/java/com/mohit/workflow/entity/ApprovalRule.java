package com.mohit.workflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "approval_rules")
public class ApprovalRule {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "workflow_definition_id", nullable = false)
    private Long workflowDefinitionId;
    
    @Column(name = "rule_name", nullable = false)
    private String ruleName;
    
    @Column(name = "entity_field")
    private String entityField;
    
    @Column(name = "operator")
    private String operator;
    
    @Column(name = "threshold_value")
    private String thresholdValue;
    
    @Column(name = "required_approver_role")
    private String requiredApproverRole;
    
    @Column(name = "approval_level")
    private Integer approvalLevel;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    



}