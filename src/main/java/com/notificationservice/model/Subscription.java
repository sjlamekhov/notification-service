package com.notificationservice.model;

import java.util.List;

public class Subscription {

    String id;

    List<Recipient> recipients;

    List<Condition> conditions;

    public Subscription() {
    }

    public Subscription(String id, List<Recipient> recipients, List<Condition> conditions) {
        this.id = id;
        this.recipients = recipients;
        this.conditions = conditions;
    }

    public String getId() {
        return id;
    }

    public Subscription setId(String id) {
        this.id = id;
        return this;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public Subscription setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
        return this;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public Subscription setConditions(List<Condition> conditions) {
        this.conditions = conditions;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (recipients != null ? !recipients.equals(that.recipients) : that.recipients != null) return false;
        return conditions != null ? conditions.equals(that.conditions) : that.conditions == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (recipients != null ? recipients.hashCode() : 0);
        result = 31 * result + (conditions != null ? conditions.hashCode() : 0);
        return result;
    }
}
