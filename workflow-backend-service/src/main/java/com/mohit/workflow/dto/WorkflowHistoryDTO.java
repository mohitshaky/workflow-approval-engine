package com.mohit.workflow.dto;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowHistoryDTO {
    private Long id;
    private String action;
    private String actorName;
    private String fromStatus;
    private String toStatus;
    private String stepName;
    private String comments;
    private LocalDateTime createdAt;
    
    // Constructors, getters, setters
}