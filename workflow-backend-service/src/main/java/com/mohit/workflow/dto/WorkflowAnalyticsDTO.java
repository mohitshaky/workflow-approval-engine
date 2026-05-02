package com.mohit.workflow.dto;

import lombok.*;

import java.util.Map;
@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowAnalyticsDTO {
    private Long pendingCount;
    private Long approvedCount;
    private Long rejectedCount;
    private Long overdueCount;
    private Double avgProcessingTimeHours;
    private Map<String, Long> entityTypeBreakdown;
//    private List<DailyStatsDTO> dailyStats;
    
    // Constructors, getters, setters
}
