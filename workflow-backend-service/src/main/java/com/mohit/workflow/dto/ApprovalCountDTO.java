package com.mohit.workflow.dto;

import lombok.Getter;

@Getter
public class ApprovalCountDTO {
    // Getters and setters
    private Long totalPending;
    private Long highPriority;
    private Long critical;
    
    public ApprovalCountDTO(Long totalPending, Long highPriority, Long critical) {
        this.totalPending = totalPending;
        this.highPriority = highPriority;
        this.critical = critical;
    }

}