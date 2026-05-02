package com.mohit.workflow.dto;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleApplicationLog {
    // Getters and setters
    private Long workflowInstanceId;
    private Long ruleId;
    private String ruleName;
    private boolean applied;
    private LocalDateTime evaluatedAt;

}