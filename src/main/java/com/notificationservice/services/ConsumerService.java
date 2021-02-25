package com.notificationservice.services;

import com.notificationservice.consumers.CustomMessage;
import com.notificationservice.consumers.kafkaConsumer.IncomingMessagesConsumer;
import com.notificationservice.model.Subscription;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ConsumerService {

    SubscriptionService subscriptionService;
    InformerService informerService;
    List<IncomingMessagesConsumer> incomingMessagesConsumers;

    public ConsumerService(
            SubscriptionService subscriptionService,
            InformerService informerService,
            List<IncomingMessagesConsumer> incomingMessagesConsumers) {
        this.subscriptionService = subscriptionService;
        this.informerService = informerService;
        this.incomingMessagesConsumers = incomingMessagesConsumers;
        for (IncomingMessagesConsumer consumer : this.incomingMessagesConsumers) {
            consumer.init();
        }
    }

    private Collection<CustomMessage> consumeFromAll() {
        List<CustomMessage> result = new ArrayList<>();
        for (IncomingMessagesConsumer consumer : incomingMessagesConsumers) {
            result.addAll(consumer.consume());
        }
        return result;
    }

    @Scheduled(fixedDelay=1000)
    public void scheduledActivity() {
        Collection<CustomMessage> received = consumeFromAll();
        for (CustomMessage message : received) {
            Map<String, Object> payload = message.getPayload();
            Collection<String> subscriptionIds = subscriptionService.getSubscriptionByAttributesAndValues(payload);
            Collection<Subscription> subscriptionsToInform = subscriptionService.getByIds(subscriptionIds);
            for (Subscription subscription : subscriptionsToInform) {
                informerService.sendNotofications(subscription, message);
            }
        }
    }

    public void close() {
        for (IncomingMessagesConsumer consumer : incomingMessagesConsumers) {
            consumer.close();
        }
    }

}
