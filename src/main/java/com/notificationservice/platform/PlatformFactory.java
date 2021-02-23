package com.notificationservice.platform;

import com.notificationservice.ConfigurationService;
import com.notificationservice.persistence.MultitablePersistence;
import com.notificationservice.persistence.converters.SubscriptionConverter;
import com.notificationservice.services.SubscriptionService;

import java.util.Properties;

public class PlatformFactory {

    public static Platform buildPlatformFromConfig(Properties properties) {
        ConfigurationService configurationService = ConfigurationService.buildConfigurationFromProperties(properties);

        MultitablePersistence multitablePersistence = new MultitablePersistence(
                configurationService.getSubscriptionsDaoConfig(),
                new SubscriptionConverter()
        );
        SubscriptionService subscriptionService = new SubscriptionService(multitablePersistence);

        return Platform.Builder
                .newInstance()
                .setSubscriptionService(subscriptionService)
                .build();
    }

}

