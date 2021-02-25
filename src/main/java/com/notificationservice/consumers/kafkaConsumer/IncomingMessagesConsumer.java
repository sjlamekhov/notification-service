package com.notificationservice.consumers.kafkaConsumer;

import com.notificationservice.consumers.CustomMessage;

import java.util.Collection;

public interface IncomingMessagesConsumer {

    void init();

    Collection<CustomMessage> consume();

    void close();

}
