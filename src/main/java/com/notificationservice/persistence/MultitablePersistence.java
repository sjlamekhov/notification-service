package com.notificationservice.persistence;

import com.mongodb.*;
import com.notificationservice.model.Subscription;
import com.notificationservice.persistence.converters.ObjectConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

//MongoDB implementation
public class MultitablePersistence {

    protected final DaoConfig configuration;
    protected final ObjectConverter<Subscription, BasicDBObject> converter;
    protected DBCollection collection;
    protected MongoClient mongoClient;

    public MultitablePersistence(DaoConfig configuration,
                                 ObjectConverter<Subscription, BasicDBObject> converter) {
        this.configuration = configuration;
        this.converter = converter;
        init();
    }

    private void init() {
        String fromConfig = configuration.getHost();
        if (null == fromConfig) {
            fromConfig = "localhost:27017";
        }
        String[] hostAndPortFromConfig = fromConfig.split(":");
        String host = hostAndPortFromConfig[0];
        int port = Integer.parseInt(hostAndPortFromConfig[1]);
        this.mongoClient = new MongoClient(host, port);
        DB database = mongoClient.getDB(configuration.getDbName());
        this.collection = database.getCollection(configuration.getTableName());
    }

    public boolean isIdPresented(String objectId) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", objectId);
        DBCursor cursor = collection.find(searchQuery);
        boolean result = cursor.hasNext();
        cursor.close();
        return result;
    }


    public Subscription add(Subscription object) {
        Objects.requireNonNull(object);
        BasicDBObject dbObject = converter.buildToFromObject(object);
        collection.insert(dbObject);
        return converter.buildObjectFromTO(dbObject);
    }

    public Subscription update(Subscription object) {
        Objects.requireNonNull(object);
        String _id = object.getId();
        BasicDBObject dbObject = converter.buildToFromObject(object);
        collection.update(
                new BasicDBObject("_id", _id),
                new BasicDBObject("$set", dbObject)
        );
        dbObject.put("_id", _id);
        return converter.buildObjectFromTO(dbObject);
    }

    public Subscription getById(String objectId) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", objectId);
        DBCursor cursor = collection.find(searchQuery);
        if (cursor.hasNext()) {
            BasicDBObject dbObject = (BasicDBObject) cursor.next();
            cursor.close();
            return converter.buildObjectFromTO(dbObject);
        }
        cursor.close();
        return null;
    }

    public Collection<Subscription> getByUris(Collection<String> ids) {
        BasicDBObject searchQuery = new BasicDBObject("_id", new BasicDBObject("$in", ids));
        DBCursor cursor = collection.find(searchQuery);
        List<Subscription> result = new ArrayList<>();
        while (cursor.hasNext()) {
            BasicDBObject dbObject = (BasicDBObject) cursor.next();
            result.add(converter.buildObjectFromTO(dbObject));
        }
        cursor.close();
        return result;
    }

    public void deleteObject(String id) {
        BasicDBObject deleteQuery = new BasicDBObject();
        deleteQuery.put("_id", id);
        collection.remove(deleteQuery);
    }

    public void close() {
        mongoClient.close();
    }
}

