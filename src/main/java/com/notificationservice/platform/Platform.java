package com.notificationservice.platform;

import com.notificationservice.services.ConsumerService;
import com.notificationservice.services.InformerService;
import com.notificationservice.services.SubscriptionService;

public class Platform {

    private final SubscriptionService subscriptionService;
    private final ConsumerService consumerService;
    private final InformerService informerService;

    private Platform(SubscriptionService subscriptionService,
                     ConsumerService consumerService,
                     InformerService informerService) {
        this.subscriptionService = subscriptionService;
        this.consumerService = consumerService;
        this.informerService = informerService;
    }

    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    public ConsumerService getConsumerService() {
        return consumerService;
    }

    public InformerService getInformerService() {
        return informerService;
    }

    public static class Builder {

        private SubscriptionService subscriptionService;
        private ConsumerService consumerService;
        private InformerService informerService;

        protected Builder() {}

        public Builder setSubscriptionService(SubscriptionService subscriptionService) {
            this.subscriptionService = subscriptionService;
            return this;
        }

        public Builder setConsumerService(ConsumerService consumerService) {
            this.consumerService = consumerService;
            return this;
        }

        public Builder setInformerService(InformerService informerService) {
            this.informerService = informerService;
            return this;
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Platform build() {
            return new Platform(
                    subscriptionService,
                    consumerService,
                    informerService
            );
        }
    }
}
