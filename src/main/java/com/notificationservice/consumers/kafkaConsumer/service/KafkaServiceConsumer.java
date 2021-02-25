package com.notificationservice.consumers.kafkaConsumer.service;

import com.notificationservice.consumers.CustomMessage;
import com.notificationservice.consumers.kafkaConsumer.deserializer.KafkaJsonDeserializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class KafkaServiceConsumer {

    private final Consumer<String, CustomMessage> consumer;

    public KafkaServiceConsumer() {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");

        consumer = new KafkaConsumer<String, CustomMessage>(props,
                new StringDeserializer(),
                new KafkaJsonDeserializer<CustomMessage>(CustomMessage.class)
        );
    }

    public Consumer consume(String topic) {
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }


}
