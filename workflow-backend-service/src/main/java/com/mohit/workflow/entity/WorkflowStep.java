package com.mohit.workflow.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "workflow_steps")
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "workflow_definition_id", nullable = false)
    private Long workflowDefinitionId;
    
    @Column(name = "step_name", nullable = false)
    private String stepName;
    
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "step_type", nullable = false)
    private StepType stepType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_type")
    private ApprovalType approvalType;
    
    @Column(name = "timeout_hours")
    private Integer timeoutHours;
    
    @Column(name = "escalation_step_id")
    private Long escalationStepId;
    
    @Type(JsonType.class)
    @Column(name = "auto_approve_conditions", columnDefinition = "json")
    private Map<String, Object> autoApproveConditions;
    
    @Type(JsonType.class)
    @Column(name = "required_roles", columnDefinition = "json")
    private List<String> requiredRoles;
    
    @Column(name = "notification_template")
    private String notificationTemplate;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public WorkflowStep(Long workflowDefinitionId, String stepName, Integer stepOrder,
                       StepType stepType, ApprovalType approvalType) {
        this.workflowDefinitionId = workflowDefinitionId;
        this.stepName = stepName;
        this.stepOrder = stepOrder;
        this.stepType = stepType;
        this.approvalType = approvalType;
    }

}
