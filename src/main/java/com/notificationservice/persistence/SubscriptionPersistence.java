package com.notificationservice.persistence;

import com.notificationservice.model.Subscription;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

public interface SubscriptionPersistence {

    Subscription add(Subscription subscription);

    Subscription update(Subscription subscription);

    Subscription getById(String subscriptionId);

    Collection<Subscription> getByIds(Collection<String> ids);

    boolean isIdPresented(String subscriptionId);

    void deleteObject(String id);

    Collection<String> getSubscriptionsByAttributesAndValues(
            Map<String, Object> attributesAndValues,
            Predicate<Subscription> subscriptionPredicate);

    void close();

}
