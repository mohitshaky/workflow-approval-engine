package com.mohit.workflow.repository;

import com.mohit.workflow.entity.WorkflowInstance;
import com.mohit.workflow.entity.WorkflowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

    public interface WorkflowInstanceRepository
            extends JpaRepository<WorkflowInstance, Long>, WorkflowInstanceRepositoryCustom {



    @Query(value = "SELECT * FROM workflow_instances WHERE entity_id = :entityId AND entity_type = :entityType AND status = CAST(:status AS workflow_status)", nativeQuery = true)
    List<WorkflowInstance> findByEntityIdAndTypeAndStatus(
            @Param("entityId") Long entityId,
            @Param("entityType") String entityType,
            @Param("status") String status
    );

    List<WorkflowInstance> findByEntityIdAndEntityTypeAndStatus(
            Long entityId,
            String entityType,
            WorkflowStatus status
    );



    List<WorkflowInstance> findByStatusAndInitiatedAtBefore(WorkflowStatus status,
                                                            LocalDateTime before);

    @Query("SELECT wi FROM WorkflowInstance wi " +
            "WHERE wi.initiatedBy = :userId AND wi.status = :status")
    Page<WorkflowInstance> findByInitiatedByAndStatus(@Param("userId") Long userId,
                                                      @Param("status") WorkflowStatus status,
                                                      Pageable pageable);

    @Query("SELECT wi FROM WorkflowInstance wi " +
            "WHERE wi.entityType = :entityType AND wi.status IN :statuses")
    List<WorkflowInstance> findByEntityTypeAndStatusIn(@Param("entityType") String entityType,
                                                       @Param("statuses") List<WorkflowStatus> statuses);

    @Query("SELECT wi FROM WorkflowInstance wi " +
            "WHERE wi.status = 'PENDING' AND wi.initiatedAt < :timeThreshold")
    List<WorkflowInstance> findOverdueInstances(@Param("timeThreshold") LocalDateTime timeThreshold);

    @Query(
            value = "SELECT COUNT(*) FROM workflow_instances wi " +
                    "WHERE wi.status = CAST(:status AS workflow_status)",
            nativeQuery = true
    )
    Long countByStatus(@Param("status") String status);


    @Query("SELECT wi.entityType, COUNT(wi) " +
            "FROM WorkflowInstance wi " +
            "WHERE wi.status = :status " +
            "GROUP BY wi.entityType")
    List<Object[]> countByStatusGroupByEntityType(@Param("status") WorkflowStatus status);

    Page<WorkflowInstance> findByInitiatedByAndStatusAndEntityType(Long userId,
                                                                   WorkflowStatus statusEnum,
                                                                   String entityType,
                                                                   Pageable pageable);

    Page<WorkflowInstance> findByInitiatedBy(Long userId, Pageable pageable);

    // ✅ Custom aggregate queries (fixed)

    @Query("SELECT COUNT(wi) FROM WorkflowInstance wi " +
            "WHERE CAST(wi.status AS string) = :status " +
            "AND wi.initiatedAt BETWEEN :fromDateTime AND :toDateTime")
    Long countByStatusAndDateRange(@Param("status") String workflowStatus,
                                   @Param("fromDateTime") LocalDateTime fromDateTime,
                                   @Param("toDateTime") LocalDateTime toDateTime);


    @Query("SELECT COUNT(wi) FROM WorkflowInstance wi " +
            "WHERE wi.status = 'PENDING' " +
            "AND wi.initiatedAt < :localDateTime")
    Long countOverdueInstances(@Param("localDateTime") LocalDateTime localDateTime);

    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (wi.completed_at - wi.initiated_at))) " +
            "FROM workflow_instances wi " +
            "WHERE wi.status = 'COMPLETED' " +
            "AND wi.completed_at BETWEEN :fromDateTime AND :toDateTime",
            nativeQuery = true)
    Double getAverageProcessingTime(@Param("fromDateTime") LocalDateTime fromDateTime,
                                    @Param("toDateTime") LocalDateTime toDateTime);


    @Query("SELECT wi.entityType, COUNT(wi) " +
            "FROM WorkflowInstance wi " +
            "WHERE wi.initiatedAt BETWEEN :fromDateTime AND :toDateTime " +
            "GROUP BY wi.entityType")
    List<Object[]> getEntityTypeBreakdown(@Param("fromDateTime") LocalDateTime fromDateTime,
                                          @Param("toDateTime") LocalDateTime toDateTime);


}
