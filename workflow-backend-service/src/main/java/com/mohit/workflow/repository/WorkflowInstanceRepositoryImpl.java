package com.mohit.workflow.repository;

import com.mohit.workflow.entity.WorkflowInstance;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class WorkflowInstanceRepositoryImpl implements WorkflowInstanceRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public WorkflowInstance saveNative(WorkflowInstance instance) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String metadataJson = objectMapper.writeValueAsString(instance.getMetadata());

            // Use entityManager, not em
            Long id = ((Number) entityManager.createNativeQuery("""
                INSERT INTO workflow_instances
                (workflow_definition_id, entity_id, entity_type, current_step_id,
                 status, priority, initiated_by, initiated_at, completed_at, metadata)
                VALUES
                (?, ?, ?, ?,
                 CAST(? AS workflow_status), CAST(? AS workflow_priority),
                 ?, ?, ?, CAST(? AS json))
                RETURNING id
            """)
                    .setParameter(1, instance.getWorkflowDefinitionId())
                    .setParameter(2, instance.getEntityId())
                    .setParameter(3, instance.getEntityType())
                    .setParameter(4, instance.getCurrentStepId())
                    .setParameter(5, instance.getStatus().name())
                    .setParameter(6, instance.getPriority().name())
                    .setParameter(7, instance.getInitiatedBy())
                    .setParameter(8, instance.getInitiatedAt())
                    .setParameter(9, instance.getCompletedAt())
                    .setParameter(10, metadataJson)
                    .getSingleResult()).longValue();

            instance.setId(id);
            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Error saving WorkflowInstance via native query", e);
        }
    }
}
