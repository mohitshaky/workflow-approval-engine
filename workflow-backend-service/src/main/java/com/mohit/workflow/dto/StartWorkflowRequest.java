package com.mohit.workflow.dto;

import jakarta.validation.constraints.NotNull;

public record StartWorkflowRequest(@NotNull Long workflowId, @NotNull String initiator) {}
