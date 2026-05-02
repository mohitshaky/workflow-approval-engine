package com.mohit.workflow.repository;
import com.mohit.workflow.entity.WorkflowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface WorkflowHistoryRepository extends JpaRepository<WorkflowHistory, Long> {
    
    List<WorkflowHistory> findByWorkflowInstanceIdOrderByCreatedAtDesc(Long workflowInstanceId);
    
    @Query("SELECT wh FROM WorkflowHistory wh WHERE wh.actorId = :actorId AND wh.createdAt BETWEEN :startDate AND :endDate")
    List<WorkflowHistory> findByActorIdAndDateRange(@Param("actorId") Long actorId, 
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT wh.action, COUNT(wh) FROM WorkflowHistory wh WHERE wh.createdAt >= :fromDate GROUP BY wh.action")
    List<Object[]> getActionStatistics(@Param("fromDate") LocalDateTime fromDate);
}