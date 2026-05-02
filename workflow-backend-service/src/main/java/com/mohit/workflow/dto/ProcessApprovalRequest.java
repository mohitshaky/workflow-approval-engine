package com.mohit.workflow.dto;

import com.mohit.workflow.entity.ApprovalStatus;
import lombok.*;

@Getter
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProcessApprovalRequest {
    private Long approvalId;
    private Long approverId;
    private ApprovalStatus decision;
    private String comments;


}