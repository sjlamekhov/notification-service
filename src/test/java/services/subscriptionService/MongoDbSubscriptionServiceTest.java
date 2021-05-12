package services.subscriptionService;

import com.notificationservice.ConfigurationService;
import com.notificationservice.persistence.DaoConfig;
import com.notificationservice.persistence.mongoDbSubscriptionPersistence.MongoDbSubscriptionPersistence;
import com.notificationservice.persistence.converters.SubscriptionDocumentConverter;
import com.notificationservice.services.SubscriptionService;
import com.notificationservice.utils.FileUtils;
import utils.MongoDbUtils;

import java.util.Properties;

public class MongoDbSubscriptionServiceTest extends SubscriptionServiceTest {

    protected SubscriptionService getSubscriptionService() {
        Properties properties = FileUtils.propertiesFromResource("mongodb.properties");
        ConfigurationService configurationService = ConfigurationService.buildConfigurationFromProperties(properties);
        DaoConfig daoConfig = configurationService.getSubscriptionsDaoConfig();
        MongoDbSubscriptionPersistence mongoDbNotificationPersistence = new MongoDbSubscriptionPersistence(
                daoConfig,
                new SubscriptionDocumentConverter()
        );
        SubscriptionService subscriptionService = new SubscriptionService(mongoDbNotificationPersistence);
        this.daoConfig = daoConfig;
        return subscriptionService;
    }

    public void after() {
        MongoDbUtils.dropCollection(daoConfig.getTableName(), daoConfig);
    }

}
