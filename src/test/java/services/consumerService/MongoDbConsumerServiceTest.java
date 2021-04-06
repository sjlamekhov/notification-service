package services.consumerService;

import com.notificationservice.ConfigurationService;
import com.notificationservice.model.RecipientType;
import com.notificationservice.persistence.DaoConfig;
import com.notificationservice.persistence.MongoDbNotificationPersistence;
import com.notificationservice.persistence.NotificationPersistence;
import com.notificationservice.persistence.converters.SubscriptionDocumentConverter;
import com.notificationservice.services.ConsumerService;
import com.notificationservice.services.InformerService;
import com.notificationservice.services.SubscriptionService;
import com.notificationservice.utils.FileUtils;
import mocks.IncomingMessagesConsumerMock;
import org.junit.After;
import org.junit.Before;
import utils.MongoDbUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

public class MongoDbConsumerServiceTest extends ConsumerServiceTest {

    protected void init() {
        Properties properties = FileUtils.propertiesFromResource("mongodb.properties");
        ConfigurationService configurationService = ConfigurationService.buildConfigurationFromProperties(properties);
        DaoConfig daoConfig = configurationService.getSubscriptionsDaoConfig();
        NotificationPersistence mongoDbNotificationPersistence = new MongoDbNotificationPersistence(
                daoConfig,
                new SubscriptionDocumentConverter()
        );
        this.daoConfig = daoConfig;
        this.subscriptionService = new SubscriptionService(mongoDbNotificationPersistence);
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
