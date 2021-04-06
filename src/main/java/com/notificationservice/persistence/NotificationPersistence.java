package com.notificationservice.persistence;

import com.notificationservice.model.Subscription;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;


public interface NotificationPersistence {

    Subscription add(Subscription subscription);

    Subscription update(Subscription subscription);

    Subscription getById(String subscriptionId);

    Collection<Subscription> getByIds(Collection<String> ids);

    boolean isIdPresented(String subscriptionId);

    void deleteObject(String id);

    Collection<Subscription> getByAttributeConditionInnerAttributes(
            String field,
            String value,
            Predicate<Subscription> subscriptionPredicate);

    Collection<Subscription> getByAttributeConditionOuterAttributes(
            Map<String, Object> attributesAndValues,
            Predicate<Subscription> subscriptionPredicate);

    void close();

}
