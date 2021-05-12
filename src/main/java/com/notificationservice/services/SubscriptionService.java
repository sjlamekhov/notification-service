package com.notificationservice.services;

import com.notificationservice.model.Condition;
import com.notificationservice.model.ConditionType;
import com.notificationservice.model.Subscription;
import com.notificationservice.ecxeptions.NotFoundException;
import com.notificationservice.persistence.SubscriptionPersistence;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class SubscriptionService {

    protected final SubscriptionPersistence subscriptionPersistence;

    public SubscriptionService(SubscriptionPersistence subscriptionPersistence) {
        this.subscriptionPersistence = subscriptionPersistence;
    }

    public Subscription create(Subscription object) {
        return subscriptionPersistence.add(object);
    }

    public Subscription update(Subscription object) {
        return subscriptionPersistence.update(object);
    }

    public boolean isIdPresented(String id) {
        return subscriptionPersistence.isIdPresented(id);
    }

    public Subscription getObject(String id) {
        if (!isIdPresented(id)) {
            throw new NotFoundException(String.format("Object with id %s does not exist", id));
        }
        return subscriptionPersistence.getById(id);
    }

    public Collection<Subscription> getByIds(Collection<String> ids) {
        return subscriptionPersistence.getByIds(ids);
    }

    public Collection<String> getSubscriptionByAttributesAndValues(Map<String, Object> attributesAndValues) {
        Predicate<Subscription> subscriptionPredicate = s -> isSubscriptionAppliable.test(s, attributesAndValues);

        return subscriptionPersistence.getSubscriptionsByAttributesAndValues(attributesAndValues, subscriptionPredicate);
    }

    public static BiPredicate<Subscription, Map<String, Object>> isSubscriptionAppliable = (subscription, attributes) -> {
        if (subscription.getConditions() == null || subscription.getConditions().isEmpty()) {
            return false;
        }
        for (Condition condition : subscription.getConditions()) {
            String field = condition.getField();
            ConditionType conditionType = condition.getConditionType();
            String value = condition.getValue();

            if (!attributes.keySet().contains(field)) {
                if (conditionType == ConditionType.NEQ_OR_NULL) {
                    continue;
                } else {
                    return false;
                }
            }
            if (!(attributes.get(field) instanceof String)) {
                continue;
            }
            String attributeValue = (String) attributes.get(field);
            if (conditionType == ConditionType.EQ) {
                if (!Objects.equals(value, attributeValue)) {
                    return false;
                }
            }
            if (conditionType == ConditionType.NE) {
                if (Objects.equals(value, attributeValue)) {
                    return false;
                }
            }
        }
        return true;
    };

    public boolean deleteObject(String id) {
        subscriptionPersistence.deleteObject(id);
        return true;
    }

    public void close() {
        subscriptionPersistence.close();
    }


}
