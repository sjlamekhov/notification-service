package com.notificationservice.platform;

import com.notificationservice.services.SubscriptionService;

public class Platform {

    private final SubscriptionService subscriptionService;

    private Platform(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    public static class Builder {

        private SubscriptionService subscriptionService;

        protected Builder() {}

        public Builder setSubscriptionService(SubscriptionService subscriptionService) {
            this.subscriptionService = subscriptionService;
            return this;
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Platform build() {
            return new Platform(
                    subscriptionService
            );
        }
    }
}
