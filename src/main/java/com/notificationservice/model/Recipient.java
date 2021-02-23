package com.notificationservice.model;

public class Recipient {

    RecipientType recipientType;
    String address;

    public Recipient() {
    }

    public Recipient(RecipientType recipientType, String address) {
        this.recipientType = recipientType;
        this.address = address;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public Recipient setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Recipient setAddress(String address) {
        this.address = address;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recipient recipient = (Recipient) o;

        if (recipientType != recipient.recipientType) return false;
        return address != null ? address.equals(recipient.address) : recipient.address == null;
    }

    @Override
    public int hashCode() {
        int result = recipientType != null ? recipientType.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
