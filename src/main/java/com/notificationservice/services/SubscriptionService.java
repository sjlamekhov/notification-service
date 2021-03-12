package com.notificationservice.services;

import com.notificationservice.model.Condition;
import com.notificationservice.model.ConditionType;
import com.notificationservice.model.Subscription;
import com.notificationservice.persistence.MultitablePersistence;
import com.notificationservice.ecxeptions.NotFoundException;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SubscriptionService {

    protected final MultitablePersistence multitablePersistence;

    public SubscriptionService(MultitablePersistence multitablePersistence) {
        this.multitablePersistence = multitablePersistence;
    }

    public Subscription create(Subscription object) {
        return multitablePersistence.add(object);
    }

    public Subscription update(Subscription object) {
        return multitablePersistence.update(object);
    }

    public boolean isIdPresented(String id) {
        return multitablePersistence.isIdPresented(id);
    }

    public Subscription getObject(String id) {
        if (!isIdPresented(id)) {
            throw new NotFoundException(String.format("Object with id %s does not exist", id));
        }
        return multitablePersistence.getById(id);
    }

    public Collection<Subscription> getByIds(Collection<String> ids) {
        return multitablePersistence.getByIds(ids);
    }

    public Collection<String> getSubscriptionByAttributesAndValues(Map<String, Object> attributesAndValues) {
        Set<String> result = new HashSet<>();
        Predicate<Subscription> subscriptionPredicate = s -> isSubscriptionAppliable.test(s, attributesAndValues);
        for (Map.Entry<String, Object> attributeItem : attributesAndValues.entrySet()) {
            if (!(attributeItem.getValue() instanceof String)) {
                continue;
            }
            Collection<Subscription> subscriptions = multitablePersistence.getByAttributeConditionInnerAttributes(
                    attributeItem.getKey(),
                    (String) attributeItem.getValue(),
                    subscriptionPredicate
            );
            result.addAll(subscriptions.stream()
                    .map(Subscription::getId)
                    .collect(Collectors.toSet()));
        }

        Collection<Subscription> subscriptions = multitablePersistence.getByAttributeConditionOuterAttributes(attributesAndValues, subscriptionPredicate);
        result.addAll(subscriptions.stream()
                .map(Subscription::getId)
                .collect(Collectors.toSet()));

        return result;
    }

    public static BiPredicate<Subscription, Map<String, Object>> isSubscriptionAppliable = (subscription, attributes) -> {
        if (subscription.getConditions() == null ||subscription.getConditions().isEmpty()) {
            return false;
        }
        for (Condition condition : subscription.getConditions()) {
            String field = condition.getField();
            ConditionType conditionType = condition.getConditionType();
            String value = condition.getValue();

            if (!attributes.keySet().contains(field)) {
                continue;
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
        multitablePersistence.deleteObject(id);
        return true;
    }

    public void close() {
        multitablePersistence.close();
    }


}
