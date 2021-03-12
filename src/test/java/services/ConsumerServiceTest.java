package services;

import com.notificationservice.ConfigurationService;
import com.notificationservice.consumers.CustomMessage;
import com.notificationservice.model.*;
import com.notificationservice.persistence.DaoConfig;
import com.notificationservice.persistence.MultitablePersistence;
import com.notificationservice.persistence.converters.SubscriptionDocumentConverter;
import com.notificationservice.services.ConsumerService;
import com.notificationservice.services.InformerService;
import com.notificationservice.services.SubscriptionService;
import com.notificationservice.utils.FileUtils;
import mocks.IncomingMessagesConsumerMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.MongoDbUtils;

import java.util.*;

public class ConsumerServiceTest {

    private DaoConfig daoConfig;
    protected SubscriptionService subscriptionService;
    protected InformerService informerService;
    protected IncomingMessagesConsumerMock incomingMessagesConsumerMock;
    protected ConsumerService consumerService;
    private List<String> fromInformerService;

    protected void init() {
        Properties properties = FileUtils.propertiesFromResource("mongodb.properties");
        ConfigurationService configurationService = ConfigurationService.buildConfigurationFromProperties(properties);
        DaoConfig daoConfig = configurationService.getSubscriptionsDaoConfig();
        MultitablePersistence multitablePersistence = new MultitablePersistence(
                daoConfig,
                new SubscriptionDocumentConverter()
        );
        this.daoConfig = daoConfig;
        this.subscriptionService = new SubscriptionService(multitablePersistence);
        this.informerService = new InformerService();
        this.fromInformerService = new ArrayList<>();
        informerService.registerConsumer(RecipientType.EMAIL,
                (recipient, customMessage) -> fromInformerService.add(
                        String.format("Sending to %s:\t%s",
                                recipient.getAddress().equals("#email#") ?
                                        customMessage.getPayload().get("email") : recipient.getAddress() ,
                                customMessage.getPayload()))
        );  //DEMO MODE
        this.incomingMessagesConsumerMock = new IncomingMessagesConsumerMock();
        this.consumerService = new ConsumerService(
                subscriptionService,
                informerService,
                Collections.singletonList(incomingMessagesConsumerMock)
        );
    }

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
    public void before() {
        init();
        MongoDbUtils.dropCollection(daoConfig.getTableName(), daoConfig);
    }

    @After
    public void after() {
        MongoDbUtils.dropCollection(daoConfig.getTableName(), daoConfig);
    }

}
