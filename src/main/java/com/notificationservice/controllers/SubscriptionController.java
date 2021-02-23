package com.notificationservice.controllers;

import com.notificationservice.model.Subscription;
import com.notificationservice.services.SubscriptionService;
import com.notificationservice.services.ecxeptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @RequestMapping(value = "/subscriptions/{subscriptionId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Subscription getById(@PathVariable("subscriptionId") String subscriptionId) {
        try {
            return subscriptionService.getObject(subscriptionId);
        } catch (NotFoundException r) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription Not Found", r);
        }
    }

    @RequestMapping(value = "/subscriptions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Subscription addSubscription(@RequestBody Subscription subscription) {
        if (null == subscription.getId()) {
            subscription.setId(UUID.randomUUID().toString());
        }
        subscriptionService.createOrUpdate(subscription);
        return subscription;
    }

    @RequestMapping(value = "/subscriptions/{subscriptionId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    public Subscription update(@PathVariable("subscriptionId") String subscriptionId,
                               @RequestBody Subscription subscription) {
        try {
            subscription.setId(subscriptionId);
            return subscriptionService.createOrUpdate(subscription);
        } catch (NotFoundException r) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription Not Found", r);
        }
    }

    @RequestMapping(value = "/subscriptions/{subscriptionId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public void deleteById(@PathVariable("subscriptionId") String subscriptionId) {
        try {
            subscriptionService.deleteObject(subscriptionId);
        } catch (NotFoundException r) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document Not Found", r);
        }
    }

}

