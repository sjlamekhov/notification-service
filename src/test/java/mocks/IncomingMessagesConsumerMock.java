package mocks;

import com.notificationservice.consumers.CustomMessage;
import com.notificationservice.consumers.kafkaConsumer.IncomingMessagesConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IncomingMessagesConsumerMock implements IncomingMessagesConsumer {

    List<CustomMessage> messagesToConsume;

    public IncomingMessagesConsumerMock() {
        this.messagesToConsume = new ArrayList<>();
    }

    public void addMessages(Collection<CustomMessage> customMessages) {
        messagesToConsume.addAll(customMessages);
    }

    @Override
    public void init() {}

    @Override
    public Collection<CustomMessage> consume() {
        List<CustomMessage> toReturn = new ArrayList<>(messagesToConsume);
        messagesToConsume.clear();
        return toReturn;
    }

    @Override
    public void close() {}
}
