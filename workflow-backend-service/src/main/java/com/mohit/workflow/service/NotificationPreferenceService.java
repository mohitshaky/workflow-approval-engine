//package com.mohit.workflow.service;
//
//import com.mohit.workflow.entity.NotificationPreference;
//import com.mohit.workflow.repository.NotificationPreferenceRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class NotificationPreferenceService {
//
//    @Autowired
//    private NotificationPreferenceRepository preferenceRepository;
//
//    public NotificationPreference getUserPreference(Long userId) {
//        return preferenceRepository.findByUserId(userId)
//            .orElse(getDefaultPreference());
//    }
//
//    private NotificationPreference getDefaultPreference() {
//        NotificationPreference preference = new NotificationPreference();
//        preference.setEmailEnabled(true);
//        preference.setSmsEnabled(false);
//        preference.setWhatsAppEnabled(false);
//        preference.setInAppEnabled(true);
//        return preference;
//    }
//}