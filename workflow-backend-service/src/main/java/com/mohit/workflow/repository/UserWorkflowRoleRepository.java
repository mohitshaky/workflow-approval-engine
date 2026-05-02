package com.mohit.workflow.repository;

import com.mohit.workflow.entity.UserWorkflowRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWorkflowRoleRepository extends JpaRepository<UserWorkflowRole, Long> {
    
    List<UserWorkflowRole> findByUserIdAndIsActive(Long userId, Boolean isActive);
    
    @Query("SELECT uwr.userId FROM UserWorkflowRole uwr WHERE uwr.roleName = :roleName AND " +
           "uwr.entityType = :entityType AND uwr.isActive = true")
    List<Long> findUserIdsByRoleAndEntityType(@Param("roleName") String roleName,
                                             @Param("entityType") String entityType);
    
    @Query("SELECT uwr FROM UserWorkflowRole uwr WHERE uwr.roleName = :roleName AND uwr.isActive = true")
    List<UserWorkflowRole> findByRoleNameAndIsActive(@Param("roleName") String roleName);
    
    Optional<UserWorkflowRole> findByUserIdAndRoleNameAndEntityType(Long userId, String roleName, String entityType);
    
    @Query("SELECT DISTINCT uwr.roleName FROM UserWorkflowRole uwr WHERE uwr.entityType = :entityType AND uwr.isActive = true")
    List<String> findDistinctRoleNamesByEntityType(@Param("entityType") String entityType);
}