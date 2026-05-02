package com.mohit.workflow.repository;

import com.mohit.workflow.dto.PendingApprovalDTO;
import com.mohit.workflow.entity.ApprovalRule;
import com.mohit.workflow.entity.ApprovalStatus;
import com.mohit.workflow.entity.WorkflowApproval;
import com.mohit.workflow.entity.WorkflowPriority;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkflowApprovalRepository extends JpaRepository<WorkflowApproval, Long>,WorkflowApprovalRepositoryCustom  {


    List<WorkflowApproval> findByApproverIdAndStatusOrderByCreatedAtDesc(Long approverId, ApprovalStatus status);


    @Query(value = "SELECT * FROM workflow_approvals wa " +
            "WHERE wa.approver_id = :userId " +
            "AND wa.status = CAST(:status AS approval_status) " +
            "ORDER BY wa.created_at",
            nativeQuery = true)
    Page<WorkflowApproval> findByApproverIdAndStatusNative(
            @Param("userId") Long userId,
            @Param("status") String status,
            Pageable pageable);



    @Query(
            value = "SELECT wa.* FROM workflow_approvals wa " +
                    "JOIN workflow_instances wi ON wa.workflow_instance_id = wi.id " +
                    "WHERE wa.approver_id = :approverId " +
                    "AND wa.status = CAST(:status AS approval_status) " +
                    "AND wi.priority = CAST(:priority AS workflow_priority)",
            countQuery = "SELECT COUNT(*) FROM workflow_approvals wa " +
                    "JOIN workflow_instances wi ON wa.workflow_instance_id = wi.id " +
                    "WHERE wa.approver_id = :approverId " +
                    "AND wa.status = CAST(:status AS approval_status) " +
                    "AND wi.priority = CAST(:priority AS workflow_priority)",
            nativeQuery = true
    )
    Page<WorkflowApproval> findApprovalsByApproverAndPriority(
            @Param("approverId") Long approverId,
            @Param("status") String status,     // Pass enum.name() as String
            @Param("priority") String priority, // Pass enum.name() as String
            Pageable pageable
    );



    @Query("SELECT wa FROM WorkflowApproval wa WHERE wa.status = :status AND wa.createdAt < :timeThreshold")
    List<WorkflowApproval> findOverdueApprovals(@Param("status") ApprovalStatus status,
                                                @Param("timeThreshold") LocalDateTime timeThreshold);

    @Query(value = "SELECT COUNT(*) FROM workflow_approvals wa " +
            "WHERE wa.approver_id = :approverId " +
            "AND wa.status = CAST(:status AS approval_status)", nativeQuery = true)
    Long countByApproverIdAndStatus(@Param("approverId") Long approverId,
                                    @Param("status") String status);



    @Query("SELECT COUNT(wa) FROM WorkflowApproval wa JOIN WorkflowInstance wi ON wa.workflowInstanceId = wi.id " +
            "WHERE wa.approverId = :approverId AND wa.status = :status AND wi.priority = :priority")
    Long countApprovalsByApproverAndPriority(@Param("approverId") Long approverId,
                                             @Param("status") ApprovalStatus status,
                                             @Param("priority") WorkflowPriority priority);

    List<WorkflowApproval> findByWorkflowInstanceId(Long instanceId);

    @Query(value = "SELECT COUNT(*) FROM workflow_approvals wa " +
            "JOIN workflow_instances wi ON wa.workflow_instance_id = wi.id " +
            "WHERE wa.approver_id = :approverId " +
            "AND wa.status = CAST('PENDING' AS approval_status) " +
            "AND wi.priority = CAST(:priority AS workflow_priority)",
            nativeQuery = true)
    Long countPendingApprovalsByApproverAndPriority(@Param("approverId") Long approverId,
                                                    @Param("priority") String priority);
    @Query(
            value = "SELECT wa.* FROM workflow_approvals wa " +
                    "JOIN workflow_instances wi ON wa.workflow_instance_id = wi.id " +
                    "WHERE wa.approver_id = :userId " +
                    "AND wa.status = CAST(:status AS approval_status) " +
                    "AND wi.priority = CAST(:priority AS workflow_priority) " +
                    "AND wi.entity_type = :entityType",
            countQuery = "SELECT count(*) FROM workflow_approvals wa " +
                    "JOIN workflow_instances wi ON wa.workflow_instance_id = wi.id " +
                    "WHERE wa.approver_id = :userId " +
                    "AND wa.status = CAST(:status AS approval_status) " +
                    "AND wi.priority = CAST(:priority AS workflow_priority) " +
                    "AND wi.entity_type = :entityType",
            nativeQuery = true
    )
    Page<WorkflowApproval> findByApproverIdAndStatusAndPriorityAndEntityType(
            @Param("userId") Long userId,
            @Param("status") String status,       // pass status.name()
            @Param("priority") String priority,   // pass priority.name()
            @Param("entityType") String entityType,
            Pageable pageable
    );



    @Query("SELECT ar FROM ApprovalRule ar WHERE ar.isActive = true AND ar.entityField = :entityField")
    List<ApprovalRule> findActiveRulesByEntityField(@Param("entityField") String entityField);
    @Modifying
    @Transactional
    @Query(value = """
    UPDATE workflow_approvals
    SET status = CAST(:status AS approval_status),
        approved_at = :approvedAt,
        comments = :comments,
        updated_at = :updatedAt
    WHERE id = :id
""", nativeQuery = true)
    void updateApprovalStatusNative(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("approvedAt") LocalDateTime approvedAt,
            @Param("comments") String comments,
            @Param("updatedAt") LocalDateTime updatedAt
    );


    @Query(
            value = "SELECT " +
                    "wa.id as id, " +
                    "wi.id as workflowInstanceId, " +
                    "wi.workflow_definition_id as workflowDefinitionId, " +
                    "wi.entity_id as entityId, " +
                    "wi.entity_type as entityType, " +
                    "ws.step_name as currentStepName, " +
                    "wi.priority as priority, " +
                    "wa.created_at as createdAt, " +
                    "EXTRACT(EPOCH FROM (NOW() - wa.created_at))/3600 as hoursWaiting, " +
                    "wi.metadata as metadata, " +
                    "wa.status as status " +
                    "FROM workflow_approvals wa " +
                    "JOIN workflow_instances wi ON wa.workflow_instance_id = wi.id " +
                    "LEFT JOIN workflow_steps ws ON wa.workflow_step_id = ws.id " +
                    "WHERE wa.approver_id = :userId " +
                    "AND wa.status = CAST(:status AS approval_status) " +
                    "AND (:priority IS NULL OR wi.priority = CAST(:priority AS workflow_priority)) " +
                    "AND (:entityType IS NULL OR wi.entity_type = :entityType) " +
                    "ORDER BY wa.created_at",
            countQuery = "SELECT count(*) " +
                    "FROM workflow_approvals wa " +
                    "JOIN workflow_instances wi ON wa.workflow_instance_id = wi.id " +
                    "WHERE wa.approver_id = :userId " +
                    "AND wa.status = CAST(:status AS approval_status) " +
                    "AND (:priority IS NULL OR wi.priority = CAST(:priority AS workflow_priority)) " +
                    "AND (:entityType IS NULL OR wi.entity_type = :entityType)",
            nativeQuery = true
    )
    Page<PendingApprovalDTO> findPendingApprovals(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("entityType") String entityType,
            Pageable pageable
    );



}
