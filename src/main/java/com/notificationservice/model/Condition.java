package com.notificationservice.model;

public class Condition {

    String field;
    ConditionType conditionType;
    String value;

    public Condition() {
    }

    public Condition(String field, ConditionType conditionType, String value) {
        this.field = field;
        this.conditionType = conditionType;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public Condition setField(String field) {
        this.field = field;
        return this;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public Condition setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Condition setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Condition condition = (Condition) o;

        if (field != null ? !field.equals(condition.field) : condition.field != null) return false;
        if (conditionType != condition.conditionType) return false;
        return value != null ? value.equals(condition.value) : condition.value == null;
    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (conditionType != null ? conditionType.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Condition{%s %s %s}", field, conditionType, value);
    }
}
