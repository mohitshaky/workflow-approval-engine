package com.mohit.workflow.dto;

import jakarta.validation.constraints.NotNull;

public record CompleteTaskRequest(@NotNull Long taskId, @NotNull String user) {}
