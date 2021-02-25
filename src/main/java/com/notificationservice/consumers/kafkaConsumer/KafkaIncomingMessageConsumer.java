package com.notificationservice.consumers.kafkaConsumer;

import com.notificationservice.consumers.CustomMessage;
import com.notificationservice.consumers.kafkaConsumer.service.KafkaServiceConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.ArrayList;
import java.util.Collection;

public class KafkaIncomingMessageConsumer implements IncomingMessagesConsumer {

    Consumer<String, CustomMessage> consumer;

    @Override
    public void init() {
        KafkaServiceConsumer kf = new KafkaServiceConsumer();
        consumer = kf.consume("msg");
    }

    @Override
    public Collection<CustomMessage> consume() {
        Collection<CustomMessage> result = new ArrayList<>();

        ConsumerRecords<String, CustomMessage> consumerRecords =
                consumer.poll(1000);
        for (ConsumerRecord<String, CustomMessage> item : consumerRecords) {
            result.add(item.value());
        }

        return result;
    }

    @Override
    public void close() {
        consumer.close();
    }

}
