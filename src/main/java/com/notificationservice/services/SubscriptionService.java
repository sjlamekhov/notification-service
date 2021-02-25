package com.notificationservice.services;

import com.notificationservice.model.Condition;
import com.notificationservice.model.ConditionType;
import com.notificationservice.model.Subscription;
import com.notificationservice.persistence.MultitablePersistence;
import com.notificationservice.ecxeptions.NotFoundException;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

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
        return multitablePersistence.getByUris(ids);
    }

    //TODO: implement support of NE conditions
    public Collection<String> getSubscriptionByAttributesAndValues(Map<String, Object> attributes) {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, Object> attributeItem : attributes.entrySet()) {
            if (!(attributeItem.getValue() instanceof String)) {
                continue;
            }
            Collection<Subscription> subscriptions = multitablePersistence.getByAttributeCondition(
                    attributeItem.getKey(),
                    (String) attributeItem.getValue()
            );
            Collection<String> filteredIds = filterSubscriptionsByAttributes(subscriptions, attributes);
            result.addAll(filteredIds);
        }
        return result;
    }

    private Collection<String> filterSubscriptionsByAttributes(Collection<Subscription> subscriptions,
                                                               Map<String, Object> attributes) {
        Collection<String> ids = new HashSet<>();
        for (Subscription subscription : subscriptions) {
            if (isSubscriptionAppliable.test(subscription, attributes)) {
                ids.add(subscription.getId());
            }
        }
        return ids;
    }

    BiPredicate<Subscription, Map<String, Object>> isSubscriptionAppliable = (subscription, attributes) -> {
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
