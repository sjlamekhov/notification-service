package com.notificationservice.persistence;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.notificationservice.model.*;
import com.notificationservice.persistence.converters.ObjectConverter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.function.Predicate;

//MongoDB implementation
public class MongoDbNotificationPersistence implements NotificationPersistence {

    protected final DaoConfig configuration;
    protected final ObjectConverter<Subscription, Document> converter;
    private MongoClient client;
    private MongoCollection<Document> collection;


    public MongoDbNotificationPersistence(DaoConfig configuration,
                                          ObjectConverter<Subscription, Document> converter) {
        this.configuration = configuration;
        this.converter = converter;
        init();
    }

    private void init() {
        String fromConfig = configuration.getHost();
        if (null == fromConfig) {
            fromConfig = "localhost:27017";
        }
        this.client = MongoClients.create(
                "mongodb://" + fromConfig
        );
        MongoDatabase database = client.getDatabase(configuration.getDbName());
        this.collection =
                database.getCollection(configuration.getTableName());

    }

    @Override
    public Subscription add(Subscription subscription) {
        Objects.requireNonNull(subscription);
        Document dbObject = converter.buildToFromObject(subscription);
        collection.insertOne(dbObject);
        return converter.buildObjectFromTO(dbObject);
    }

    @Override
    public Subscription update(Subscription subscription) {
        Objects.requireNonNull(subscription);
        String _id = subscription.getId();
        Document query = new Document();
        query.put("_id", _id);
        Document dbObject = converter.buildToFromObject(subscription);
        dbObject.put("_id", _id);
        collection.updateOne(
                query, dbObject
        );
        return converter.buildObjectFromTO(dbObject);
    }

    @Override
    public Subscription getById(String subscriptionId) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", subscriptionId);
        FindIterable<Document> result = collection.find(searchQuery);
        Subscription subscriptionResult = null;
        for (Document document : result) {
            subscriptionResult = converter.buildObjectFromTO(document);
            break;
        }
        return subscriptionResult;
    }

    @Override
    public Collection<Subscription> getByIds(Collection<String> ids) {
        BasicDBObject searchQuery = new BasicDBObject("_id", new BasicDBObject("$in", ids));
        FindIterable<Document> result = collection.find(searchQuery);
        List<Subscription> subscriptions = new ArrayList<>();
        for (Document document : result) {
            subscriptions.add(converter.buildObjectFromTO(document));
        }
        return subscriptions;
    }

    @Override
    public boolean isIdPresented(String subscriptionId) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", subscriptionId);
        FindIterable<Document> iterable = collection.find(searchQuery);
        for (Document document : iterable) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteObject(String id) {
        BasicDBObject deleteQuery = new BasicDBObject();
        deleteQuery.put("_id", id);
        collection.deleteOne(deleteQuery);
    }

    @Override
    public void close() {
        client.close();
    }

    @Override
    public Collection<Subscription> getByAttributeConditionInnerAttributes(
            String field,
            String value,
            Predicate<Subscription> subscriptionPredicate) {
        Collection<Subscription> result = new HashSet<>();

        BasicDBObject subQueryEq = new BasicDBObject();
        subQueryEq.put("conditions.field", field);
        subQueryEq.put("conditions.conditionType", "EQ");
        subQueryEq.put("conditions.value", value);

        BasicDBObject subQueryNeq = new BasicDBObject();
        subQueryNeq.put("conditions.field", field);
        subQueryNeq.put("conditions.conditionType", "NE");

        FindIterable<Document> findIterable = collection.find(Filters.or(
               Filters.or(subQueryEq, subQueryNeq)
        ));
        for (Document document : findIterable) {
            Subscription converted = converter.buildObjectFromTO(document);
            if (subscriptionPredicate.test(converted)) {
                result.add(converted);
            }
        }

        return result;
    }

    @Override
    public Collection<Subscription> getByAttributeConditionOuterAttributes(
            Map<String, Object> attributesAndValues,
            Predicate<Subscription> subscriptionPredicate) {
        Collection<Subscription> result = new HashSet<>();

        Bson[] fieldsQueries = new Bson[attributesAndValues.keySet().size() + 1];
        fieldsQueries[0] = new BasicDBObject("conditionType", "NE");
        int counter = 1;
        for (String attribute : attributesAndValues.keySet()) {
            fieldsQueries[counter] = Filters.not(new BasicDBObject("field", attribute));
            counter++;
        }

        FindIterable<Document> findIterable = collection.find(Filters.or(
                Filters.elemMatch("conditions",
                        Filters.and(
                                fieldsQueries
                        ))
        ));

        for (Document document : findIterable) {
            Subscription converted = converter.buildObjectFromTO(document);
            if (subscriptionPredicate.test(converted)) {
                result.add(converted);
            }
        }
        return result;
    }

}

