package com.mohit.workflow.repository;

import com.mohit.workflow.entity.ApprovalRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ApprovalRuleRepository extends JpaRepository<ApprovalRule, Long> {

    /**
     * Find active approval rules for a workflow, ordered by approval level
     */
    List<ApprovalRule> findByWorkflowDefinitionIdAndIsActiveOrderByApprovalLevel(
            Long workflowDefinitionId, Boolean isActive);

    /**
     * Find all approval rules for a workflow
     */
    List<ApprovalRule> findByWorkflowDefinitionId(Long workflowDefinitionId);

    /**
     * Find all active rules by entity field
     */
    @Query("SELECT ar FROM ApprovalRule ar WHERE ar.isActive = true AND ar.entityField = :entityField")
    List<ApprovalRule> findActiveRulesByEntityField(@Param("entityField") String entityField);

}
