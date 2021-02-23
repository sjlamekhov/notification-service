package services;

import com.notificationservice.ConfigurationService;
import com.notificationservice.model.*;
import com.notificationservice.persistence.DaoConfig;
import com.notificationservice.persistence.MultitablePersistence;
import com.notificationservice.persistence.converters.SubscriptionConverter;
import com.notificationservice.services.SubscriptionService;
import com.notificationservice.utils.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.MongoDbUtils;

import java.util.Collections;
import java.util.Properties;

public class SubscriptionServiceTest {

    private final SubscriptionService subscriptionService;
    private DaoConfig daoConfig;

    public SubscriptionServiceTest() {
        this.subscriptionService = getSubscriptionService();
    }

    protected SubscriptionService getSubscriptionService() {
        Properties properties = FileUtils.propertiesFromResource("mongodb.properties");
        ConfigurationService configurationService = ConfigurationService.buildConfigurationFromProperties(properties);
        DaoConfig daoConfig = configurationService.getSubscriptionsDaoConfig();
        MultitablePersistence multitablePersistence = new MultitablePersistence(
                daoConfig,
                new SubscriptionConverter()
        );
        SubscriptionService subscriptionService = new SubscriptionService(multitablePersistence);
        this.daoConfig = daoConfig;
        return subscriptionService;
    }

    @Test
    public void createAndGetSubscriptionTest() {
        Subscription subscription = new Subscription();
        subscription.setRecipients(Collections.singletonList(new Recipient(RecipientType.EMAIL, "someAddress")));
        subscription.setConditions(Collections.singletonList(new Condition("field", ConditionType.EQ, "value")));

        Subscription postedSubscription = subscriptionService.createOrUpdate(subscription);
        Assert.assertNotNull(postedSubscription);
        Assert.assertNotNull(postedSubscription.getId());

        Assert.assertEquals(subscription.getConditions(), postedSubscription.getConditions());
        Assert.assertEquals(subscription.getRecipients(), postedSubscription.getRecipients());
    }

    @Before
    @After
    public void after() {
        MongoDbUtils.dropCollection(daoConfig.getTableName(), daoConfig);
    }

}
