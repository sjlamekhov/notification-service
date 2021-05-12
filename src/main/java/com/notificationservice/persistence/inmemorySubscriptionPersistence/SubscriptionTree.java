package com.notificationservice.persistence.inmemorySubscriptionPersistence;

import com.notificationservice.model.Condition;
import com.notificationservice.model.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionTree {

    private Map<Condition, SubscriptionTree> node;
    private boolean isTerminal;
    private List<String> subscriptionsIds;

    public SubscriptionTree() {
        this.node = new HashMap<>();
        this.isTerminal = false;
        this.subscriptionsIds = new ArrayList<>();
    }

    public Map<Condition, SubscriptionTree> getNode() {
        return node;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public void setTerminal(boolean isTerminal) {
        this.isTerminal = isTerminal;
    }

    public List<String> getSubscriptions() {
        return subscriptionsIds;
    }
}
