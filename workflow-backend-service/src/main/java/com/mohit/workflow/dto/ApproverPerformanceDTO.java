package com.mohit.workflow.dto;

import lombok.*;

@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApproverPerformanceDTO {
    private Long approverId;
    private String approverName;
    private Long totalApprovals;
    private Long approvedCount;
    private Long rejectedCount;
    private Double avgProcessingTimeHours;
    private Long overdueCount;
    
    // Constructors, getters, setters
}