package com.mohit.workflow.repository;
import com.mohit.workflow.entity.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, Long> {
    
    Optional<WorkflowDefinition> findByEntityTypeAndIsActive(String entityType, Boolean isActive);
    
    List<WorkflowDefinition> findByIsActive(Boolean isActive);
    
    @Query("SELECT wd FROM WorkflowDefinition wd WHERE wd.name LIKE %:name% AND wd.isActive = true")
    List<WorkflowDefinition> findByNameContainingAndIsActive(@Param("name") String name);
}