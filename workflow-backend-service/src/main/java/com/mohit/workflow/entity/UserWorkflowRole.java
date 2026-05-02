package com.mohit.workflow.entity;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;
@Entity
@Table(name = "user_workflow_roles")
public class UserWorkflowRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "role_name", nullable = false)
    private String roleName;
    
    @Column(name = "entity_type", nullable = false)
    private String entityType;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "assigned_by")
    private Long assignedBy;
    
    @CreationTimestamp
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    // Constructors
    public UserWorkflowRole() {}
    
    public UserWorkflowRole(Long userId, String roleName, String entityType, Long assignedBy) {
        this.userId = userId;
        this.roleName = roleName;
        this.entityType = entityType;
        this.assignedBy = assignedBy;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }
    
    public LocalDateTime getAssignedAt() { return assignedAt; }
}
