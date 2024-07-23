package com.bod.bod.challenge.entity;

public enum ConditionStatus {

    BEFORE(ConditionType.BEFORE),
    TODO(ConditionType.TODO),
    COMPLETE(ConditionType.COMPLETE);

    private final String conditionStatus;

    ConditionStatus(String conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public String getConditionStatus() {return this.conditionStatus;}

    public static class ConditionType{
        public static final String BEFORE = "대기";
        public static final String TODO = "진행중";
        public static final String COMPLETE = "마감";
    }
}
