package com.mohit.workflow.entity;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Table(name = "workflow_instances")
public class WorkflowInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "workflow_definition_id", nullable = false)
    private Long workflowDefinitionId;
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Column(name = "entity_type", nullable = false)
    private String entityType;
    
    @Column(name = "current_step_id")
    private Long currentStepId;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private WorkflowPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkflowStatus status;
    
    @Column(name = "initiated_by", nullable = false)
    private Long initiatedBy;
    
    @CreationTimestamp
    @Column(name = "initiated_at")
    private LocalDateTime initiatedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Type(JsonType.class)
    @Column(name = "metadata", columnDefinition = "json")
    private Map<String, Object> metadata;

    public WorkflowInstance(Long workflowDefinitionId, Long entityId, String entityType, Long initiatedBy) {
        this.workflowDefinitionId = workflowDefinitionId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.initiatedBy = initiatedBy;
    }


}
