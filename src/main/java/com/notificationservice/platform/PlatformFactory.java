package com.notificationservice.platform;

import com.notificationservice.ConfigurationService;
import com.notificationservice.consumers.kafkaConsumer.IncomingMessagesConsumer;
import com.notificationservice.consumers.kafkaConsumer.KafkaIncomingMessageConsumer;
import com.notificationservice.model.RecipientType;
import com.notificationservice.persistence.mongoDbSubscriptionPersistence.MongoDbSubscriptionPersistence;
import com.notificationservice.persistence.converters.SubscriptionDocumentConverter;
import com.notificationservice.services.ConsumerService;
import com.notificationservice.services.InformerService;
import com.notificationservice.services.SubscriptionService;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PlatformFactory {

    public static Platform buildPlatformFromConfig(Properties properties) {
        ConfigurationService configurationService = ConfigurationService.buildConfigurationFromProperties(properties);

        MongoDbSubscriptionPersistence mongoDbNotificationPersistence = new MongoDbSubscriptionPersistence(
                configurationService.getSubscriptionsDaoConfig(),
                new SubscriptionDocumentConverter()
        );
        SubscriptionService subscriptionService = new SubscriptionService(mongoDbNotificationPersistence);

        List<IncomingMessagesConsumer> incomingMessagesConsumers = Arrays.asList(
                new KafkaIncomingMessageConsumer()
        );

        InformerService informerService = new InformerService();
        informerService.registerConsumer(RecipientType.EMAIL,
                (recipient, customMessage) -> System.out.println(
                        String.format("Sending to %s:\t%s",
                                recipient.getAddress().equals("#email#") ?
                                        customMessage.getPayload().get("email") : recipient.getAddress() ,
                                customMessage.getPayload()))
        );  //DEMO MODE

        ConsumerService consumerService = new ConsumerService(
                subscriptionService,
                informerService,
                incomingMessagesConsumers);

        return Platform.Builder
                .newInstance()
                .setSubscriptionService(subscriptionService)
                .setConsumerService(consumerService)
                .setInformerService(informerService)
                .build();
    }

}

