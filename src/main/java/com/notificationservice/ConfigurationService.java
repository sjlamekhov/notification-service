package com.notificationservice;

import com.notificationservice.persistence.DaoConfig;
import com.notificationservice.persistence.DaoType;

import java.util.*;

public class ConfigurationService {

    private DaoConfig subscriptionsDaoConfig;

    public DaoConfig getSubscriptionsDaoConfig() {
        return subscriptionsDaoConfig;
    }

    public ConfigurationService setSubscriptionsDaoConfig(DaoConfig subscriptionsDaoConfig) {
        this.subscriptionsDaoConfig = subscriptionsDaoConfig;
        return this;
    }

    public static ConfigurationService buildConfigurationFromProperties(Properties platformProperties) {
        ConfigurationService configurationService = new ConfigurationService();

        DaoConfig subscriptionDaoConfig = new DaoConfig(
                platformProperties.getProperty("tableName", "subscriptions"),
                DaoType.MONGODB,
                platformProperties.getProperty("host", "localhost:27017"),
                platformProperties.getProperty("dbName", "subscriptionservice")
        );

        configurationService.setSubscriptionsDaoConfig(subscriptionDaoConfig);

        return configurationService;
    }

}