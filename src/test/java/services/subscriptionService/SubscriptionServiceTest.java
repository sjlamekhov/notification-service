package services.subscriptionService;

import com.notificationservice.model.*;
import com.notificationservice.persistence.DaoConfig;
import com.notificationservice.services.SubscriptionService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.MongoDbUtils;

import java.util.*;

public abstract class SubscriptionServiceTest {

    protected final SubscriptionService subscriptionService;
    protected DaoConfig daoConfig;

    public SubscriptionServiceTest() {
        this.subscriptionService = getSubscriptionService();
    }

    protected abstract SubscriptionService getSubscriptionService();

    @Before
    @After
    public abstract void after();

    @Test
    public void createAndGetSubscriptionTest() {
        Subscription subscription = new Subscription();
        subscription.setRecipients(Collections.singletonList(new Recipient(RecipientType.EMAIL, "someAddress")));
        subscription.setConditions(Collections.singletonList(new Condition("field", ConditionType.EQ, "value")));

        Subscription postedSubscription = subscriptionService.create(subscription);
        Assert.assertNotNull(postedSubscription);
        Assert.assertNotNull(postedSubscription.getId());

        Assert.assertEquals(subscription.getConditions(), postedSubscription.getConditions());
        Assert.assertEquals(subscription.getRecipients(), postedSubscription.getRecipients());
    }

    @Test
    public void getSubscriptionByAttributesAndValuesEqTest() {
        Subscription matchByField = new Subscription(
                UUID.randomUUID().toString(),
                Collections.emptyList(),
                Collections.singletonList(new Condition(
                        "field", ConditionType.EQ, "otherValue"
                )));
        subscriptionService.create(matchByField);
        Subscription matchByValue = new Subscription(
                UUID.randomUUID().toString(),
                Collections.emptyList(),
                Collections.singletonList(new Condition(
                        "otherField", ConditionType.EQ, "value"
                )));
        subscriptionService.create(matchByValue);
        Subscription completeMatch = new Subscription(
                UUID.randomUUID().toString(),
                Collections.emptyList(),
                Collections.singletonList(new Condition(
                        "field", ConditionType.EQ, "value"
                )));
        subscriptionService.create(completeMatch);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("field", "value");
        Collection<String> result = subscriptionService.getSubscriptionByAttributesAndValues(attributes);

        Assert.assertTrue(result.contains(completeMatch.getId()));
        Assert.assertFalse(result.contains(matchByField.getId()));
        Assert.assertFalse(result.contains(matchByValue.getId()));
    }

    @Test
    public void getSubscriptionByAttributesAndValuesNeqTest() {
        Subscription matchByFieldEq = new Subscription(
                UUID.randomUUID().toString(),
                Collections.emptyList(),
                Collections.singletonList(new Condition(
                        "field1", ConditionType.EQ, "value"
                )));
        subscriptionService.create(matchByFieldEq);

        Subscription matchByFieldNeq = new Subscription(
                UUID.randomUUID().toString(),
                Collections.emptyList(),
                Collections.singletonList(new Condition(
                        "field1", ConditionType.NE, "value"
                )));
        subscriptionService.create(matchByFieldNeq);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("field1", "neqValue");
        attributes.put("field2", "someValue");

        Collection<String> result = subscriptionService.getSubscriptionByAttributesAndValues(attributes);

        Assert.assertEquals(1, result.size());
        Assert.assertFalse(result.contains(matchByFieldEq.getId()));
        Assert.assertTrue(result.contains(matchByFieldNeq.getId()));
    }

    @Test
    public void getSubscriptionByAttributesAndValuesAllNeqTest() {
        Subscription matchByFieldNeq = new Subscription(
                UUID.randomUUID().toString(),
                Collections.emptyList(),
                Collections.singletonList(new Condition(
                        "field", ConditionType.NE, "value"
                )));
        subscriptionService.create(matchByFieldNeq);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("field1", "value1");
        attributes.put("field2", "value2");

        Collection<String> result = subscriptionService.getSubscriptionByAttributesAndValues(attributes);

        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains(matchByFieldNeq.getId()));
    }

}
