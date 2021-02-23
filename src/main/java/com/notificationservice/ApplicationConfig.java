package com.notificationservice;

import com.notificationservice.platform.ConfigProvider;
import com.notificationservice.platform.Platform;
import com.notificationservice.platform.PlatformFactory;
import com.notificationservice.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Properties;

@Configuration
public class ApplicationConfig {

    @Autowired
    private Platform platform;

    private final Properties properties;

    public ApplicationConfig() {
        properties = ConfigProvider.getProperties();
    }

    @Bean(name = "platform")
    public Platform getPlatform() {
        return PlatformFactory.buildPlatformFromConfig(properties);
    }

    @Bean(name = "subscriptionService", destroyMethod = "close")
    @DependsOn("platform")
    public SubscriptionService getSubscriptionService() {
        return platform.getSubscriptionService();
    }

}

