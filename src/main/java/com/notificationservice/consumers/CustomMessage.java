package com.notificationservice.consumers;

import java.util.HashMap;
import java.util.Map;

public class CustomMessage {

    private String id;
    private String type;
    private Map<String, Object> payload = new HashMap<>();

    @Override
    public String toString() {
        return "CustomMessage{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }

    public CustomMessage() {
    }

    public CustomMessage(String id, String type, Map<String, Object> payload) {
        this.id = id;
        this.type = type;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public CustomMessage setId(String id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public CustomMessage setType(String type) {
        this.type = type;
        return this;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public CustomMessage setPayload(Map<String, Object> payload) {
        this.payload = payload;
        return this;
    }
}
