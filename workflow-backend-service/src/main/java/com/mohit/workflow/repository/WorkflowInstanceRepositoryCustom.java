package com.mohit.workflow.repository;

import com.mohit.workflow.entity.WorkflowInstance;

public interface WorkflowInstanceRepositoryCustom {
    WorkflowInstance saveNative(WorkflowInstance instance);
}
