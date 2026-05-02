package com.mohit.workflow.repository;

import com.mohit.workflow.entity.WorkflowApproval;

import java.util.List;

public interface WorkflowApprovalRepositoryCustom {
    void insertApproval(WorkflowApproval approval);

    List<WorkflowApproval> findByWorkflowInstanceIdAndWorkflowStepIdNative(Long instanceId, Long stepId);
}
