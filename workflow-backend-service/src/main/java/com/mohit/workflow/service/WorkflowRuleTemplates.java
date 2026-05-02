//package com.mohit.workflow.service;
//
//import com.mohit.workflow.dto.ApprovalRuleTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class WorkflowRuleTemplates {
//
//    /**
//     * Stock update rule templates
//     */
//    public static final Map<String, ApprovalRuleTemplate> STOCK_UPDATE_TEMPLATES = Map.of(
//        "HIGH_QUANTITY_CHANGE", new ApprovalRuleTemplate(
//            "High Quantity Change",
//            "quantityDifference",
//            "GT",
//            "1000",
//            "SUPER_ADMIN",
//            2,
//            "Requires super admin approval for quantity changes > 1000 units"
//        ),
//
//        "HIGH_VALUE_PRODUCT", new ApprovalRuleTemplate(
//            "High Value Product Update",
//            "productValue",
//            "GT",
//            "50000",
//            "FINANCE_MANAGER",
//            2,
//            "Requires finance manager approval for high value products"
//        ),
//
//        "EXPIRY_SOON", new ApprovalRuleTemplate(
//            "Near Expiry Product",
//            "daysToExpiry",
//            "LT",
//            "30",
//            "QUALITY_MANAGER",
//            1,
//            "Special handling for products expiring within 30 days"
//        )
//    );
//
//    /**
//     * Buyer onboarding rule templates
//     */
//    public static final Map<String, ApprovalRuleTemplate> BUYER_ONBOARDING_TEMPLATES = Map.of(
//        "HIGH_CREDIT_LIMIT", new ApprovalRuleTemplate(
//            "High Credit Limit Request",
//            "requestedCreditLimit",
//            "GT",
//            "1000000",
//            "BOARD_MEMBER",
//            3,
//            "Board approval required for credit limits > 10L"
//        ),
//
//        "ENTERPRISE_CUSTOMER", new ApprovalRuleTemplate(
//            "Enterprise Customer Onboarding",
//            "businessType",
//            "IN",
//            "ENTERPRISE,GOVERNMENT,MNC",
//            "SENIOR_MANAGER",
//            2,
//            "Senior management approval for enterprise customers"
//        ),
//
//        "HIGH_RISK_INDUSTRY", new ApprovalRuleTemplate(
//            "High Risk Industry",
//            "industryRiskLevel",
//            "EQ",
//            "HIGH",
//            "RISK_MANAGER",
//            2,
//            "Risk manager approval for high-risk industries"
//        )
//    );
//
//    /**
//     * Get rule template by type and name
//     */
//    public ApprovalRuleTemplate getTemplate(String entityType, String templateName) {
//        switch (entityType.toUpperCase()) {
//            case "STOCK_UPDATE":
//                return STOCK_UPDATE_TEMPLATES.get(templateName);
//            case "BUYER_ONBOARDING":
//                return BUYER_ONBOARDING_TEMPLATES.get(templateName);
//            default:
//                return null;
//        }
//    }
//
//    /**
//     * Get all templates for an entity type
//     */
//    public Map<String, ApprovalRuleTemplate> getTemplatesForEntityType(String entityType) {
//        switch (entityType.toUpperCase()) {
//            case "STOCK_UPDATE":
//                return STOCK_UPDATE_TEMPLATES;
//            case "BUYER_ONBOARDING":
//                return BUYER_ONBOARDING_TEMPLATES;
//            default:
//                return new HashMap<>();
//        }
//    }
//}