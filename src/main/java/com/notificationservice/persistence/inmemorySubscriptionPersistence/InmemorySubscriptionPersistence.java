package com.notificationservice.persistence.inmemorySubscriptionPersistence;

import com.notificationservice.model.Condition;
import com.notificationservice.model.ConditionType;
import com.notificationservice.model.Subscription;
import com.notificationservice.persistence.SubscriptionPersistence;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InmemorySubscriptionPersistence implements SubscriptionPersistence {

    final Map<String, Subscription> idToSubscription;
    final SubscriptionTree subscriptionTreeRoot;

    public InmemorySubscriptionPersistence() {
        idToSubscription = new HashMap<>();
        subscriptionTreeRoot = new SubscriptionTree();
    }

    @Override
    public Subscription add(Subscription subscription) {
        if (null == subscription.getId()) {
            subscription.setId(UUID.randomUUID().toString());
        }
        idToSubscription.put(subscription.getId(), subscription);
        addSubscriptionInternal(subscription);
        return subscription;
    }

    private void addSubscriptionInternal(Subscription subscription) {
        List<String> fieldNames = new ArrayList<>();
        Map<String, Condition> fieldNameToConditionMap = new HashMap<>();
        getSubscriptionsFields(subscription, fieldNames, fieldNameToConditionMap);
        addSubscriptionToTree(subscriptionTreeRoot, subscription, fieldNames, fieldNameToConditionMap);
    }

    private void addSubscriptionToTree(SubscriptionTree subscriptionTreeRoot,
                                       Subscription subscription,
                                       List<String> fieldNames,
                                       Map<String, Condition> fieldNameToConditionMap) {
        SubscriptionTree currentSubscriptionTree = subscriptionTreeRoot;
        for (int fieldIndex = 0; fieldIndex < fieldNames.size(); fieldIndex++) {
            String currentFieldName = fieldNames.get(fieldIndex);
            Condition currentCondition = fieldNameToConditionMap.get(currentFieldName);
            if (null == currentSubscriptionTree.getNode().get(currentCondition)) {
                SubscriptionTree newSubscriptionTree = new SubscriptionTree();
                if (fieldIndex == fieldNames.size() - 1) {
                    newSubscriptionTree.setTerminal(true);
                    newSubscriptionTree.getSubscriptions().add(subscription.getId());
                }
                currentSubscriptionTree.getNode().put(currentCondition, newSubscriptionTree);
            } else {
                SubscriptionTree subscriptionTree = currentSubscriptionTree.getNode().get(currentCondition);
                if (fieldIndex == fieldNames.size() - 1) {
                    subscriptionTree.setTerminal(true);
                    subscriptionTree.getSubscriptions().add(subscription.getId());
                }
            }
            currentSubscriptionTree = currentSubscriptionTree.getNode().get(currentCondition);
        }
    }

    @Override
    public Subscription update(Subscription subscription) {
        Subscription exisitngSubscription = idToSubscription.get(subscription.getId());
        deleteSubscriptionInternal(exisitngSubscription);
        idToSubscription.put(subscription.getId(), subscription);
        addSubscriptionInternal(subscription);
        return subscription;
    }

    @Override
    public Subscription getById(String subscriptionId) {
        return idToSubscription.get(subscriptionId);
    }

    @Override
    public Collection<Subscription> getByIds(Collection<String> ids) {
        Collection<Subscription> result = new ArrayList<>();
        for (String id : ids) {
            Subscription fetched = idToSubscription.get(id);
            if (null != fetched) {
                result.add(fetched);
            }
        }
        return result;
    }

    @Override
    public boolean isIdPresented(String subscriptionId) {
        return idToSubscription.containsKey(subscriptionId);
    }

    @Override
    public void deleteObject(String subscriptionId) {
        Subscription existingSubscription = idToSubscription.get(subscriptionId);
        deleteSubscriptionInternal(existingSubscription);
    }

    private void deleteSubscriptionInternal(Subscription subscription) {
        List<String> fieldNames = new ArrayList<>();
        Map<String, Condition> fieldNameToConditionMap = new HashMap<>();
        getSubscriptionsFields(subscription, fieldNames, fieldNameToConditionMap);
        deleteSubscriptionFromTree(subscriptionTreeRoot, subscription, fieldNames, fieldNameToConditionMap, 0);
    }

    private void deleteSubscriptionFromTree(SubscriptionTree currentSubscriptionTree,
                                            Subscription subscription,
                                            List<String> fieldNames,
                                            Map<String, Condition> fieldNameToConditionMap,
                                            int fieldIndex) {
        if (fieldIndex == fieldNames.size()) {
            currentSubscriptionTree.getSubscriptions().remove(subscription.getId());
            if (currentSubscriptionTree.getSubscriptions().isEmpty()) {
                currentSubscriptionTree.setTerminal(false);
            }
        } else {
            String currentFieldName = fieldNames.get(fieldIndex);
            Condition currentCondition = fieldNameToConditionMap.get(currentFieldName);
            SubscriptionTree nextNode = currentSubscriptionTree.getNode().get(currentCondition);
            deleteSubscriptionFromTree(nextNode, subscription, fieldNames, fieldNameToConditionMap, fieldIndex + 1);
            if (nextNode.getNode().isEmpty() && nextNode.getSubscriptions().isEmpty()) {
                currentSubscriptionTree.getNode().remove(currentCondition);
            }
        }
    }

    @Override
    public Collection<String> getSubscriptionsByAttributesAndValues(Map<String, Object> attributesAndValues, Predicate<Subscription> subscriptionPredicate) {
        Set<String> subscriptionIdsToCheck = new HashSet<>();
        List<String> fieldNames = attributesAndValues.keySet().stream().sorted().collect(Collectors.toList());
        getSubscriptionsByAttributesAndValuesInternal(subscriptionTreeRoot, attributesAndValues, fieldNames, 0, subscriptionIdsToCheck);
        Set<String> result = new HashSet<>();
        for (String idToCheck : subscriptionIdsToCheck) {
            if (subscriptionPredicate.test(getById(idToCheck))) {
                result.add(idToCheck);
            }
        }
        return result;
    }

    private void getSubscriptionsByAttributesAndValuesInternal(SubscriptionTree currentSubscriptionTree,
                                                               Map<String, Object> attributesAndValues,
                                                               List<String> fieldNames,
                                                               int fieldIndex,
                                                               Set<String> subscriptionIdsToCheck) {
        if (currentSubscriptionTree.isTerminal()) {
            subscriptionIdsToCheck.addAll(currentSubscriptionTree.getSubscriptions());
        }
        if (fieldIndex < fieldNames.size()) {
            String currentFieldName = fieldNames.get(fieldIndex);
            String currentFieldValue = (String) attributesAndValues.get(currentFieldName);
            //check if we can add ids to result
            //EQ conditionType
            Condition eqCondition = new Condition(currentFieldName, ConditionType.EQ, currentFieldValue);
            SubscriptionTree nextTreeByEq = currentSubscriptionTree.getNode().get(eqCondition);
            if (null != nextTreeByEq) {
                getSubscriptionsByAttributesAndValuesInternal(nextTreeByEq, attributesAndValues, fieldNames, fieldIndex + 1, subscriptionIdsToCheck);
            }
            //NE, NEQ_OR_NULL conditionType
            for (Map.Entry<Condition, SubscriptionTree> item : currentSubscriptionTree.getNode().entrySet()) {
                Condition condition = item.getKey();
                boolean canGoFurther = false;
                if (condition.getConditionType().equals(ConditionType.NE) && !Objects.equals(condition.getValue(), currentFieldValue)) {
                    canGoFurther = true;
                }
                if (condition.getConditionType().equals(ConditionType.NEQ_OR_NULL) && (!Objects.equals(condition.getValue(), currentFieldValue) || null == currentFieldValue)) {
                    canGoFurther = true;
                }
                if (canGoFurther) {
                    getSubscriptionsByAttributesAndValuesInternal(item.getValue(), attributesAndValues, fieldNames, fieldIndex + 1, subscriptionIdsToCheck);
                }
            }
        } else {
            //NE, NEQ_OR_NULL conditionType
            for (Map.Entry<Condition, SubscriptionTree> item : currentSubscriptionTree.getNode().entrySet()) {
                Condition condition = item.getKey();
                boolean canGoFurther = false;
                if (condition.getConditionType() == ConditionType.NE
                        && attributesAndValues.containsKey(condition.getField()) && !Objects.equals(attributesAndValues.get(condition.getField()), condition.getValue())) {
                    canGoFurther = true;
                }
                if (condition.getConditionType() == ConditionType.NEQ_OR_NULL && !attributesAndValues.containsKey(condition.getField())) {
                    canGoFurther |= true;
                }
                if (canGoFurther) {
                    getSubscriptionsByAttributesAndValuesInternal(item.getValue(), attributesAndValues, fieldNames, fieldIndex + 1, subscriptionIdsToCheck);
                }
            }
        }
    }

    @Override
    public void close() {
    }

    private static void getSubscriptionsFields(Subscription subscription,
                                               List<String> fieldNames,
                                               Map<String, Condition> fieldNameToConditionMap) {
        for (Condition condition : subscription.getConditions()) {
            fieldNames.add(condition.getField());
            fieldNameToConditionMap.put(condition.getField(), condition);
        }
        Collections.sort(fieldNames);
    }
}
