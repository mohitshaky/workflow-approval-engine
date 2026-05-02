package com.mohit.workflow.repository;
import com.mohit.workflow.entity.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {
    
    List<WorkflowStep> findByWorkflowDefinitionIdOrderByStepOrder(Long workflowDefinitionId);
    
    @Query("SELECT ws FROM WorkflowStep ws WHERE ws.workflowDefinitionId = :workflowDefinitionId ORDER BY ws.stepOrder ASC")
    List<WorkflowStep> findByWorkflowDefinitionId(@Param("workflowDefinitionId") Long workflowDefinitionId);
    
    @Query("SELECT ws FROM WorkflowStep ws WHERE ws.workflowDefinitionId = :workflowDefinitionId AND ws.stepOrder = 1")
    Optional<WorkflowStep> findFirstStepByWorkflowDefinitionId(@Param("workflowDefinitionId") Long workflowDefinitionId);
    
    @Query("SELECT ws FROM WorkflowStep ws WHERE ws.workflowDefinitionId = :workflowDefinitionId AND ws.stepOrder = " +
           "(SELECT curr.stepOrder + 1 FROM WorkflowStep curr WHERE curr.id = :currentStepId)")
    Optional<WorkflowStep> findNextStep(@Param("workflowDefinitionId") Long workflowDefinitionId, 
                                       @Param("currentStepId") Long currentStepId);
    
    @Query("SELECT ws FROM WorkflowStep ws WHERE ws.workflowDefinitionId = :workflowDefinitionId AND ws.stepOrder = " +
           "(SELECT curr.stepOrder - 1 FROM WorkflowStep curr WHERE curr.id = :currentStepId)")
    Optional<WorkflowStep> findPreviousStep(@Param("workflowDefinitionId") Long workflowDefinitionId, 
                                           @Param("currentStepId") Long currentStepId);
}