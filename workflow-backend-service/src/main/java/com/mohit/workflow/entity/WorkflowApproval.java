package com.mohit.workflow.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@SqlResultSetMapping(
        name = "WorkflowApprovalMapping",
        entities = @EntityResult(
                entityClass = WorkflowApproval.class,
                fields = {
                        @FieldResult(name = "id", column = "id"),
                        @FieldResult(name = "workflowInstanceId", column = "workflow_instance_id"),
                        @FieldResult(name = "workflowStepId", column = "workflow_step_id"),
                        @FieldResult(name = "approverId", column = "approver_id"),
                        @FieldResult(name = "status", column = "status"),
                        @FieldResult(name = "comments", column = "comments"),
                        @FieldResult(name = "approvedAt", column = "approved_at"),
                        @FieldResult(name = "createdAt", column = "created_at"),
                        @FieldResult(name = "escalatedAt", column = "escalated_at"),
                        @FieldResult(name = "escalatedTo", column = "escalated_to"),
                        @FieldResult(name = "updatedAt", column = "updated_at")
                }
        )
)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "workflow_approvals")
public class WorkflowApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "workflow_instance_id", nullable = false)
    private Long workflowInstanceId;

    @Column(name = "workflow_step_id", nullable = false)
    private Long workflowStepId;

    @Column(name = "approver_id", nullable = false)
    private Long approverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "approval_status", nullable = false)
    private ApprovalStatus status;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "escalated_at")
    private LocalDateTime escalatedAt;

    @Column(name = "escalated_to")
    private Long escalatedTo;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public WorkflowApproval(Long workflowInstanceId, Long workflowStepId, Long approverId) {
        this.workflowInstanceId = workflowInstanceId;
        this.workflowStepId = workflowStepId;
        this.approverId = approverId;
    }

}
