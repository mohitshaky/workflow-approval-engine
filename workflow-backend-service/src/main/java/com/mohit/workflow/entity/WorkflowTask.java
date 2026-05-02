package com.mohit.workflow.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "workflow_task")
@Data
public class WorkflowTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id")
    private WorkflowInstance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private WorkflowStep step;

    @Column(name="assigned_user")
    private String assignedUser;

    @Column(nullable=false)
    private String status; // PENDING, COMPLETED, ESCALATED

    @Column(name="started_at")
    private OffsetDateTime startedAt;

    @Column(name="completed_at")
    private OffsetDateTime completedAt;

    @Column(name="due_at")
    private OffsetDateTime dueAt;

    @Column(name="created_at")
    private OffsetDateTime createdAt;

    @Column(name="updated_at")
    private OffsetDateTime updatedAt;
}
