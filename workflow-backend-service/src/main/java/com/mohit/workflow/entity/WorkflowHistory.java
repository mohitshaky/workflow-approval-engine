package com.mohit.workflow.entity;
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
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workflow_history")
public class WorkflowHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "workflow_instance_id", nullable = false)
    private Long workflowInstanceId;
    
    @Column(name = "action", nullable = false)
    private String action;
    
    @Column(name = "actor_id")
    private Long actorId;
    
    @Column(name = "from_status")
    private String fromStatus;
    
    @Column(name = "to_status")
    private String toStatus;
    
    @Column(name = "step_name")
    private String stepName;
    
    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;
    
    @Type(JsonType.class)
    @Column(name = "metadata", columnDefinition = "json")
    private Map<String, Object> metadata;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
