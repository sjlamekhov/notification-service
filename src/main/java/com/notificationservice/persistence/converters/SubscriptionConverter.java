package com.notificationservice.persistence.converters;

import com.mongodb.BasicDBObject;
import com.notificationservice.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SubscriptionConverter implements ObjectConverter<Subscription, BasicDBObject> {

    Function<BasicDBObject, Recipient> recipientConverter = to -> {
        Recipient recipient = new Recipient();
        recipient.setAddress(to.getString("address"));
        String typeFromTO = to.getString("type", "EMAIL");
        recipient.setRecipientType(
                RecipientType.EMAIL
        );
        return recipient;
    };

    Function<BasicDBObject, Condition> conditionConverter = to -> {
        Condition condition = new Condition();
        condition.setField(to.getString("field"));
        condition.setConditionType("EQ".equals(to.getString("conditionType")) ? ConditionType.EQ : ConditionType.NE);
        condition.setValue(to.getString("value"));
        return condition;
    };

    Function<Recipient, BasicDBObject> fromRecipientConverter = r -> {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("type", r.getRecipientType().name());
        basicDBObject.put("address", r.getAddress());
        return basicDBObject;
    };

    Function<Condition, BasicDBObject> fromConditionConverter = c -> {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("field", c.getField());
        basicDBObject.put("conditionType", c.getConditionType().name());
        basicDBObject.put("value", c.getValue());
        return basicDBObject;
    };

    private static final Set<String> attributeNamesToSkip = new HashSet<>(Arrays.asList(
            "_id"
    ));

    @Override
    public Subscription buildObjectFromTO(BasicDBObject transferObject) {
        String subscriptionId = null;
        if (transferObject.containsField("_id")) {
            subscriptionId = transferObject.getString("_id");
        }
        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        if (transferObject.containsKey("recipients")) {
            List<Recipient> recipients = new ArrayList<>();
            for (Object o : (Collection)transferObject.get("recipients")) {
                recipients.add(recipientConverter.apply((BasicDBObject) o));
            }
            subscription.setRecipients(recipients);
        }
        if (transferObject.containsKey("conditions")) {
            List<Condition> conditions = new ArrayList<>();
            for (Object o : (Collection)transferObject.get("conditions")) {
                conditions.add(conditionConverter.apply((BasicDBObject) o));
            }
            subscription.setConditions(conditions);
        }
        return subscription;
    }

    @Override
    public BasicDBObject buildToFromObject(Subscription subscription) {
        BasicDBObject basicDBObject = new BasicDBObject();
        if (subscription.getId() != null) {
            basicDBObject.put("_id", subscription.getId());
        }
        if (null != subscription.getConditions()) {
            basicDBObject.put("conditions", subscription.getConditions().stream()
                    .map(fromConditionConverter).collect(Collectors.toList()));
        }
        if (null != subscription.getRecipients()) {
            basicDBObject.put("recipients", subscription.getRecipients().stream()
                    .map(fromRecipientConverter).collect(Collectors.toList()));
        }
        return basicDBObject;
    }

}
