package com.mohit.workflow.repository;

import com.mohit.workflow.entity.WorkflowApproval;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkflowApprovalRepositoryImpl implements WorkflowApprovalRepositoryCustom {

    private final EntityManager em;

    @Transactional
    @Override
    public void insertApproval(WorkflowApproval approval) {
        em.createNativeQuery("""
            INSERT INTO workflow_approvals (
                workflow_instance_id,
                workflow_step_id,
                approver_id,
                status,
                comments,
                created_at
            ) VALUES (?1, ?2, ?3, CAST(?4 AS approval_status), ?5, NOW())
        """)
        .setParameter(1, approval.getWorkflowInstanceId())
        .setParameter(2, approval.getWorkflowStepId())
        .setParameter(3, approval.getApproverId())
        .setParameter(4, approval.getStatus().name())  // 👈 enum to string
        .setParameter(5, approval.getComments())
        .executeUpdate();
    }
    @Override
    public List<WorkflowApproval> findByWorkflowInstanceIdAndWorkflowStepIdNative(Long instanceId, Long stepId) {
        return em.createNativeQuery("""
            SELECT 
                id, 
                approved_at, 
                approver_id, 
                comments, 
                created_at, 
                escalated_at,
                escalated_to, 
                status::text AS status,  -- Cast enum to text
                updated_at, 
                workflow_instance_id, 
                workflow_step_id
            FROM workflow_approvals
            WHERE workflow_instance_id = :instanceId
              AND workflow_step_id = :stepId
        """, "WorkflowApprovalMapping") // Make sure @SqlResultSetMapping exists in WorkflowApproval entity
                .setParameter("instanceId", instanceId)
                .setParameter("stepId", stepId)
                .getResultList();
    }
    @Transactional
    public void updateApprovalNative(WorkflowApproval approval) {
        em.createNativeQuery("""
        UPDATE workflow_approvals
        SET 
            approved_at = :approvedAt,
            approver_id = :approverId,
            comments = :comments,
            created_at = :createdAt,
            escalated_at = :escalatedAt,
            escalated_to = :escalatedTo,
            status = CAST(:status AS approval_status), -- Cast to enum
            updated_at = :updatedAt,
            workflow_instance_id = :instanceId,
            workflow_step_id = :stepId
        WHERE id = :id
    """)
                .setParameter("approvedAt", approval.getApprovedAt())
                .setParameter("approverId", approval.getApproverId())
                .setParameter("comments", approval.getComments())
                .setParameter("createdAt", approval.getCreatedAt())
                .setParameter("escalatedAt", approval.getEscalatedAt())
                .setParameter("escalatedTo", approval.getEscalatedTo())
                .setParameter("status", approval.getStatus().name()) // pass enum name
                .setParameter("updatedAt", approval.getUpdatedAt())
                .setParameter("instanceId", approval.getWorkflowInstanceId())
                .setParameter("stepId", approval.getWorkflowStepId())
                .setParameter("id", approval.getId())
                .executeUpdate();
    }

}
