//package com.mohit.workflow.service;
//
//import com.mohit.workflow.entity.WorkflowTask;
//import com.mohit.workflow.repository.WorkflowTaskRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.OffsetDateTime;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class EscalationScheduler {
//
//    private final WorkflowTaskRepository taskRepo;
//
//    @Scheduled(fixedRateString = "60000") // every 1 minute
//    @Transactional
//    public void checkSlas() {
//        List<WorkflowTask> overdue = taskRepo.findByStatusAndDueAtBefore("PENDING", OffsetDateTime.now());
//        for (WorkflowTask t : overdue) {
//            t.setStatus("ESCALATED");
//            t.setUpdatedAt(OffsetDateTime.now());
//            taskRepo.save(t);
//        }
//    }
//}
