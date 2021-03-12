package com.notificationservice.persistence.converters;

import com.notificationservice.model.*;
import org.bson.Document;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SubscriptionDocumentConverter implements ObjectConverter<Subscription, Document> {

    Function<Document, Recipient> recipientConverter = to -> {
        Recipient recipient = new Recipient();
        recipient.setAddress(to.getString("address"));
        String typeFromTO = to.getString("type");
        recipient.setRecipientType(
                RecipientType.EMAIL
        );
        return recipient;
    };

    Function<Document, Condition> conditionConverter = to -> {
        Condition condition = new Condition();
        condition.setField(to.getString("field"));
        condition.setConditionType("EQ".equals(to.getString("conditionType")) ? ConditionType.EQ : ConditionType.NE);
        condition.setValue(to.getString("value"));
        return condition;
    };

    Function<Recipient, Document> fromRecipientConverter = r -> {
        Document document = new Document();
        document.put("type", r.getRecipientType().name());
        document.put("address", r.getAddress());
        return document;
    };

    Function<Condition, Document> fromConditionConverter = c -> {
        Document document = new Document();
        document.put("field", c.getField());
        document.put("conditionType", c.getConditionType().name());
        document.put("value", c.getValue());
        return document;
    };

    private static final Set<String> attributeNamesToSkip = new HashSet<>(Arrays.asList(
            "_id"
    ));

    @Override
    public Subscription buildObjectFromTO(Document transferObject) {
        String subscriptionId = null;
        if (transferObject.containsKey("_id")) {
            subscriptionId = transferObject.get("_id").toString();
        }
        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        if (transferObject.containsKey("recipients")) {
            List<Recipient> recipients = new ArrayList<>();
            for (Object o : (Collection)transferObject.get("recipients")) {
                recipients.add(recipientConverter.apply((Document) o));
            }
            subscription.setRecipients(recipients);
        }
        if (transferObject.containsKey("conditions")) {
            List<Condition> conditions = new ArrayList<>();
            for (Object o : (Collection)transferObject.get("conditions")) {
                conditions.add(conditionConverter.apply((Document) o));
            }
            subscription.setConditions(conditions);
        }
        return subscription;
    }

    @Override
    public Document buildToFromObject(Subscription subscription) {
        Document document = new Document();
        if (subscription.getId() != null) {
            document.put("_id", subscription.getId());
        }
        if (null != subscription.getConditions()) {
            document.put("conditions", subscription.getConditions().stream()
                    .map(fromConditionConverter).collect(Collectors.toList()));
        }
        if (null != subscription.getRecipients()) {
            document.put("recipients", subscription.getRecipients().stream()
                    .map(fromRecipientConverter).collect(Collectors.toList()));
        }
        return document;
    }

}
