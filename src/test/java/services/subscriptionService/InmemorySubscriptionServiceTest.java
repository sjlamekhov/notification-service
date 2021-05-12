package services.subscriptionService;

import com.notificationservice.model.*;
import com.notificationservice.persistence.inmemorySubscriptionPersistence.InmemorySubscriptionPersistence;
import com.notificationservice.services.SubscriptionService;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class InmemorySubscriptionServiceTest extends SubscriptionServiceTest {

    @Test
    public void addSubscriptionTest() {
        SubscriptionService subscriptionService = new SubscriptionService(
                new InmemorySubscriptionPersistence()
        );

        Subscription subscription1 = new Subscription();
        subscription1.setId(UUID.randomUUID().toString());
        subscription1.setRecipients(Collections.singletonList(
                new Recipient(RecipientType.EMAIL, "someRecipient1@mail.com")
        ));
        subscription1.setConditions(Arrays.asList(
                new Condition("a", ConditionType.EQ, "av"),
                new Condition("b", ConditionType.EQ, "bv"),
                new Condition("c", ConditionType.NE, "ev")
        ));
        subscriptionService.create(subscription1);

        Subscription subscription2 = new Subscription();
        subscription2.setId(UUID.randomUUID().toString());
        subscription2.setRecipients(Collections.singletonList(
                new Recipient(RecipientType.EMAIL, "someRecipient2@mail.com")
        ));
        subscription2.setConditions(Arrays.asList(
                new Condition("a", ConditionType.EQ, "av"),
                new Condition("b", ConditionType.NE, "bv"),
                new Condition("c", ConditionType.NE, "ev")
        ));
        subscriptionService.create(subscription2);

        subscriptionService.deleteObject(subscription1.getId());

    }

    @Override
    protected SubscriptionService getSubscriptionService() {
        SubscriptionService subscriptionService = new SubscriptionService(
            new InmemorySubscriptionPersistence()
        );
        return subscriptionService;
    }

    @Override
    public void after() {
    }
}
