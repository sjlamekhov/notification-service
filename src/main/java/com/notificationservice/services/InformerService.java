package com.notificationservice.services;

import com.notificationservice.consumers.CustomMessage;
import com.notificationservice.kafkaConsumer.consumer.ConsumerApp;
import com.notificationservice.model.Recipient;
import com.notificationservice.model.RecipientType;
import com.notificationservice.model.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

//mocked for now, need to implement sending
public class InformerService {

    private static Logger logger = LoggerFactory.getLogger(InformerService.class);

    private Map<RecipientType, BiConsumer<Recipient, CustomMessage>> recipientsAndActions;

    public InformerService() {
        recipientsAndActions = new HashMap<>();
        recipientsAndActions.put(RecipientType.EMAIL,
                (recipient, customMessage) -> System.out.println(
                        String.format("Sending to %s:\t%s", recipient.getAddress(), customMessage))
        );
    }

    public void sendNotofications(Subscription subscription, CustomMessage message) {
        for (Recipient recipient : subscription.getRecipients()) {
            BiConsumer<Recipient, CustomMessage> action = recipientsAndActions.get(recipient.getRecipientType());
            if (null == action) {
                logger.error(String.format("Notification action for %s not found", recipient));
            } else {
                action.accept(recipient, message);
            }
        }
    }

}
