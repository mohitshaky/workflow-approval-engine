//package com.mohit.workflow.service;
//
//import com.mohit.workflow.entity.*;
//import com.mohit.workflow.repository.WorkflowDefinitionRepository;
//import com.mohit.workflow.repository.WorkflowInstanceRepository;
//import com.mohit.workflow.repository.WorkflowTaskRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.OffsetDateTime;
//import java.util.Comparator;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class WorkflowService {
//
//    private final WorkflowDefinitionRepository definitionRepo;
//    private final WorkflowInstanceRepository instanceRepo;
//    private final WorkflowTaskRepository taskRepo;
//
//    @Transactional
//    public WorkflowInstance startWorkflow(Long workflowId, String initiator) {
//        WorkflowDefinition definition = definitionRepo.findById(workflowId)
//                .orElseThrow(() -> new IllegalArgumentException("Workflow not found"));
//
//        WorkflowInstance instance = new WorkflowInstance();
//        instance.setWorkflow(definition);
//        instance.setStatus(WorkflowStatus.valueOf("IN_PROGRESS"));
//        instance.setCreatedAt(OffsetDateTime.now());
//        instance.setUpdatedAt(OffsetDateTime.now());
//        WorkflowInstance saved = instanceRepo.save(instance);
//
//        WorkflowStep firstStep = definition.getSteps().stream()
//                .min(Comparator.comparingInt(WorkflowStep::getStepOrder))
//                .orElseThrow(() -> new IllegalStateException("No steps defined"));
//
//        createTask(saved, firstStep, initiator);
//        return saved;
//    }
//
//    @Transactional
//    public void completeTask(Long taskId, String user) {
//        WorkflowTask task = taskRepo.findById(taskId)
//                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
//
//        if (task.getAssignedUser() != null && !task.getAssignedUser().equals(user)) {
//            throw new IllegalStateException("Unauthorized to complete this task");
//        }
//
//        task.setStatus("COMPLETED");
//        task.setCompletedAt(OffsetDateTime.now());
//        task.setUpdatedAt(OffsetDateTime.now());
//        taskRepo.save(task);
//
//        WorkflowInstance instance = task.getInstance();
//        WorkflowDefinition definition = instance.getWorkflow();
//
//        Optional<WorkflowStep> nextStep = definition.getSteps().stream()
//                .filter(s -> s.getStepOrder() > task.getStep().getStepOrder())
//                .min(Comparator.comparingInt(WorkflowStep::getStepOrder));
//
//        if (nextStep.isPresent()) {
//            createTask(instance, nextStep.get(), user);
//        } else {
//            instance.setStatus(WorkflowStatus.valueOf("COMPLETED"));
//            instance.setUpdatedAt(OffsetDateTime.now());
//            instanceRepo.save(instance);
//        }
//    }
//
//    private void createTask(WorkflowInstance instance, WorkflowStep step, String user) {
//        WorkflowTask task = new WorkflowTask();
//        task.setInstance(instance);
//        task.setStep(step);
//        task.setAssignedUser(user);
//        task.setStatus("PENDING");
//        task.setStartedAt(OffsetDateTime.now());
//        task.setCreatedAt(OffsetDateTime.now());
//        task.setUpdatedAt(OffsetDateTime.now());
//        if (step.getSlaMinutes() != null) {
//            task.setDueAt(OffsetDateTime.now().plusMinutes(step.getSlaMinutes()));
//        }
//        taskRepo.save(task);
//    }
//}
