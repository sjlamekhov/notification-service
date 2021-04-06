package services.consumerService;

import com.notificationservice.consumers.CustomMessage;
import com.notificationservice.model.*;
import com.notificationservice.persistence.DaoConfig;
import com.notificationservice.services.ConsumerService;
import com.notificationservice.services.InformerService;
import com.notificationservice.services.SubscriptionService;
import mocks.IncomingMessagesConsumerMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public abstract class ConsumerServiceTest {

    protected DaoConfig daoConfig;
    protected SubscriptionService subscriptionService;
    protected InformerService informerService;
    protected IncomingMessagesConsumerMock incomingMessagesConsumerMock;
    protected ConsumerService consumerService;
    protected List<String> fromInformerService;

    protected abstract void init();

    @Test
    public void testOfEmailing() {
        subscriptionService.create(new Subscription(
                UUID.randomUUID().toString(),
                Collections.singletonList(new Recipient(RecipientType.EMAIL, "#email#")),
                Collections.singletonList(new Condition("field", ConditionType.EQ, "value"))
        ));
        incomingMessagesConsumerMock.addMessages(Arrays.asList(new CustomMessage(
                UUID.randomUUID().toString(), "type",
                new HashMap() {{
                    put("field", "value");
                    put("email", "emailAddress");
                }})));
        consumerService.scheduledActivity();
        Assert.assertEquals(1, fromInformerService.size());
        Assert.assertEquals("Sending to emailAddress:\t{field=value, email=emailAddress}",
                fromInformerService.iterator().next());
    }

    @Before
    public abstract void before();

    @After
    public abstract void after();

}
