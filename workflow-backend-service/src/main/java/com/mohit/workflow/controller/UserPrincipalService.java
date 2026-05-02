//package com.mohit.workflow.controller;
//
//import com.mohit.workflow.dto.CustomUserDetails;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserPrincipalService {
//
//    /**
//     * Extracts user ID from Authentication object
//     */
//    public Long getCurrentUserId(Authentication authentication) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return null; // or throw exception
//        }
//
//        // Assuming you have a custom UserDetails
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        return userDetails.getId();
//    }
//}
