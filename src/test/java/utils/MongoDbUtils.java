package utils;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.notificationservice.persistence.DaoConfig;

public class MongoDbUtils {

    public static void dropCollection(String collectionName, DaoConfig daoConfig) {
        String fromConfig = daoConfig.getHost();
        if (null == fromConfig) {
            fromConfig = "localhost:27017";
        }
        String[] hostAndPortFromConfig = fromConfig.split(":");
        String host = hostAndPortFromConfig[0];
        int port = Integer.parseInt(hostAndPortFromConfig[1]);
        MongoClient mongoClient = new MongoClient(host, port);
        DB database = mongoClient.getDB(daoConfig.getDbName());
        if (database.getCollectionNames().contains(collectionName)) {
            DBCollection collection = database.getCollection(collectionName);
            collection.drop();
        }
        mongoClient.close();
    }

}
