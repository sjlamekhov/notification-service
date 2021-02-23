package com.notificationservice.persistence.converters;

public interface ObjectConverter<T, TO> {

    T buildObjectFromTO(TO transferObject);

    TO buildToFromObject(T subscription);

}
