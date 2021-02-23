package com.notificationservice.services;

import com.notificationservice.model.Subscription;
import com.notificationservice.persistence.MultitablePersistence;
import com.notificationservice.services.ecxeptions.NotFoundException;

import java.util.Collection;

public class SubscriptionService {

    protected final MultitablePersistence multitablePersistence;

    public SubscriptionService(MultitablePersistence multitablePersistence) {
        this.multitablePersistence = multitablePersistence;
    }

    public Subscription createOrUpdate(Subscription object) {
        return multitablePersistence.add(object);
    }

    public boolean isIdPresented(String id) {
        return multitablePersistence.isIdPresented(id);
    }

    public Subscription getObject(String id) {
        if (!isIdPresented(id)) {
            throw new NotFoundException(String.format("Object with id %s does not exist", id));
        }
        return multitablePersistence.getById(id);
    }

    public Collection<Subscription> getByIds(Collection<String> ids) {
        return multitablePersistence.getByUris(ids);
    }

    public boolean deleteObject(String id) {
        multitablePersistence.deleteObject(id);
        return true;
    }

    public void close() {
        multitablePersistence.close();
    }


}
