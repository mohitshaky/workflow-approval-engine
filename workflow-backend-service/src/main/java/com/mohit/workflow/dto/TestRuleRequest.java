package com.mohit.workflow.dto;

import java.util.Map;

public class TestRuleRequest {
    private String entityField;
    private String operator;
    private String thresholdValue;
    private Map<String, Object> testData;
    
    // Getters and setters
    public String getEntityField() { return entityField; }
    public void setEntityField(String entityField) { this.entityField = entityField; }
    
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    
    public String getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(String thresholdValue) { this.thresholdValue = thresholdValue; }
    
    public Map<String, Object> getTestData() { return testData; }
    public void setTestData(Map<String, Object> testData) { this.testData = testData; }
}