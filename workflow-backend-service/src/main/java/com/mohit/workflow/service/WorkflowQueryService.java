package com.mohit.workflow.service;

import com.mohit.workflow.dto.*;
import com.mohit.workflow.entity.*;
import com.mohit.workflow.exception.WorkflowInstanceNotFoundException;
import com.mohit.workflow.exception.WorkflowNotFoundException;
import com.mohit.workflow.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional()
public class WorkflowQueryService {
    
    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;
    
    @Autowired
    private WorkflowApprovalRepository workflowApprovalRepository;
    
    @Autowired
    private WorkflowHistoryRepository workflowHistoryRepository;
    
    @Autowired
    private WorkflowDefinitionRepository workflowDefinitionRepository;
   @Autowired
   private WorkflowStepRepository workflowStepRepository;
    
    /**
     * Get pending approvals for a user
     */
    public Page<PendingApprovalDTO> getPendingApprovalsForUser(
            Long userId,
            String priority,
            String entityType,
            Pageable pageable) {

        String status = ApprovalStatus.PENDING.name();

        String priorityValue = (priority != null && !priority.isBlank()) ?
                WorkflowPriority.valueOf(priority.toUpperCase()).name() : null;

        entityType = (entityType != null && entityType.isBlank()) ? null : entityType;

        return workflowApprovalRepository.findPendingApprovals(
                userId, status, priorityValue, entityType, pageable
        );
    }





    /**
     * Get pending approvals count for a user
     */
    public ApprovalCountDTO getPendingApprovalsCount(Long userId) {

        String status = ApprovalStatus.PENDING.name();

        Long totalCount = workflowApprovalRepository
                .countByApproverIdAndStatus(userId, status);

        Long highPriorityCount = workflowApprovalRepository
                .countPendingApprovalsByApproverAndPriority(userId, WorkflowPriority.HIGH.name());

        Long criticalCount = workflowApprovalRepository
                .countPendingApprovalsByApproverAndPriority(userId, WorkflowPriority.CRITICAL.name());

        return new ApprovalCountDTO(totalCount, highPriorityCount, criticalCount);
    }

    
    /**
     * Get workflow instance details
     */
    public WorkflowInstanceDetailDTO getWorkflowInstanceDetails(Long instanceId) {
        WorkflowInstance instance = workflowInstanceRepository
            .findById(instanceId)
            .orElseThrow(() -> new WorkflowInstanceNotFoundException("Instance not found"));
        
        WorkflowDefinition definition = workflowDefinitionRepository
            .findById(instance.getWorkflowDefinitionId())
            .orElseThrow(() -> new WorkflowNotFoundException("Definition not found"));
        
        List<WorkflowApproval> approvals = workflowApprovalRepository
            .findByWorkflowInstanceId(instanceId);
        
        return convertToDetailDTO(instance, definition, approvals);
    }
    
    /**
     * Get workflow history
     */
    public List<WorkflowHistoryDTO> getWorkflowHistory(Long instanceId) {
        List<WorkflowHistory> history = workflowHistoryRepository
            .findByWorkflowInstanceIdOrderByCreatedAtDesc(instanceId);
        
        return history.stream()
            .map(this::convertToHistoryDTO)
            .collect(Collectors.toList());
    }

    
    /**
     * Get workflow analytics
     */
    public WorkflowAnalyticsDTO getWorkflowAnalytics(String entityType, LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : LocalDateTime.now().minusDays(30);
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(23, 59, 59) : LocalDateTime.now();
        
        WorkflowAnalyticsDTO analytics = new WorkflowAnalyticsDTO();
        
        // Get basic counts
        analytics.setPendingCount(workflowInstanceRepository.countByStatus(String.valueOf(WorkflowStatus.PENDING)));
        analytics.setApprovedCount(workflowInstanceRepository.countByStatusAndDateRange(
                String.valueOf(WorkflowStatus.APPROVED), fromDateTime, toDateTime));
        analytics.setRejectedCount(workflowInstanceRepository.countByStatusAndDateRange(
                String.valueOf(WorkflowStatus.REJECTED), fromDateTime, toDateTime));
        
        // Get overdue count
        analytics.setOverdueCount(workflowInstanceRepository.countOverdueInstances(LocalDateTime.now().minusHours(48)));
        
        // Get average processing time
        analytics.setAvgProcessingTimeHours(workflowInstanceRepository.getAverageProcessingTime(fromDateTime, toDateTime));
        
        // Get entity type breakdown
        List<Object[]> breakdownList = workflowInstanceRepository.getEntityTypeBreakdown(fromDateTime, toDateTime);

        Map<String, Long> breakdown = breakdownList.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],   // entityType
                        row -> (Long) row[1]      // count
                ));

        analytics.setEntityTypeBreakdown(breakdown);

        
        return analytics;
    }







    private WorkflowInstanceDetailDTO convertToDetailDTO(WorkflowInstance instance, 
                                                        WorkflowDefinition definition,
                                                        List<WorkflowApproval> approvals
                                                       ) {
        String currentStepName = approvals.stream()
                .max(Comparator.comparing(WorkflowApproval::getCreatedAt))
                .map(wa -> workflowStepRepository
                        .findById(wa.getWorkflowStepId())
                        .map(ws -> ws.getStepName())
                        .orElse("Unknown Step"))
                .orElse("No Steps");

        // 2️⃣ InitiatedByName placeholder
        String initiatedByName = "User-" + instance.getInitiatedBy();
        WorkflowInstanceDetailDTO dto = new WorkflowInstanceDetailDTO();
        dto.setId(instance.getId());
        dto.setWorkflowName(definition.getName());
        dto.setEntityType(instance.getEntityType());
        dto.setEntityId(instance.getEntityId());
        dto.setStatus(instance.getStatus());
        dto.setPriority(instance.getPriority());
        dto.setInitiatedByName(initiatedByName);
        dto.setInitiatedAt(instance.getInitiatedAt());
        dto.setCompletedAt(instance.getCompletedAt());
        dto.setCurrentStepName(currentStepName);
        dto.setMetadata(instance.getMetadata());

        return dto;
    }

    private WorkflowHistoryDTO convertToHistoryDTO(WorkflowHistory history) {
        String actorName = history.getActorId() != null ? STR."User-\{history.getActorId()}" : "System";

        return new WorkflowHistoryDTO(
                history.getId(),
                history.getAction(),
                actorName,
                history.getFromStatus(),
                history.getToStatus(),
                history.getStepName(),
                history.getComments(),
                history.getCreatedAt()
        );
    }



    private WorkflowInstanceSummaryDTO convertToSummaryDTO(WorkflowInstance instance) {
        // Implementation details...
        return new WorkflowInstanceSummaryDTO(/* mapping fields */);
    }
    

}
