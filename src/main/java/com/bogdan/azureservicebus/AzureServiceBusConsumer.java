package com.bogdan.azureservicebus;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceiverAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AzureServiceBusConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureServiceBusConsumer.class);

    private final ServiceBusReceiverAsyncClient receiverQueueClient;
    private final ServiceBusReceiverAsyncClient receiverTopicSub1Client;
    private final ServiceBusReceiverAsyncClient receiverTopicSub2Client;

    private static final List<String> RECEIVED_MESSAGES_QUEUE = new CopyOnWriteArrayList<>();
    private static final List<String> RECEIVED_MESSAGES_TOPIC_1 = new CopyOnWriteArrayList<>();
    private static final List<String> RECEIVED_MESSAGES_TOPIC_ALL = new CopyOnWriteArrayList<>();

    public AzureServiceBusConsumer(
            @Value("${azure.servicebus.connection-string}") String connectionString,
            @Value("${azure.servicebus.queue-name}") String queueName,
            @Value("${azure.servicebus.topic-name}") String topicName
    ) {
        this.receiverQueueClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .queueName(queueName)
                .buildAsyncClient();
        receiverQueueClient.receiveMessages().onBackpressureBuffer(1000)
                .doOnNext(message -> {
                            RECEIVED_MESSAGES_QUEUE.add(message.getBody().toString());
                            LOGGER.info("Received message from queue: {}", message.getBody());
                        }
                )
                .subscribe();
        this.receiverTopicSub1Client = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .topicName(topicName)
                .subscriptionName("subscription.1")
                .buildAsyncClient();
        receiverTopicSub1Client.receiveMessages().onBackpressureBuffer(1000)
                .doOnNext(message -> {
                            RECEIVED_MESSAGES_TOPIC_1.add(message.getBody().toString());
                            LOGGER.info("Received message from topic sub 1: {}", message.getBody());
                        }
                )
                .subscribe();

        this.receiverTopicSub2Client = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .topicName(topicName)
                .subscriptionName("subscription.2")
                .buildAsyncClient();
        receiverTopicSub2Client.receiveMessages().onBackpressureBuffer(1000)
                .doOnNext(message -> {
                            RECEIVED_MESSAGES_TOPIC_ALL.add(message.getBody().toString());
                            LOGGER.info("Received message from topic sub 2: {}", message.getBody());
                        }
                )
                .subscribe();
    }

    public List<String> receiveMessagesQueue() {
        return RECEIVED_MESSAGES_QUEUE;
    }

    public List<String> receiveMessagesTopic(String subId) {
        if ("all".equals(subId)) {
            return RECEIVED_MESSAGES_TOPIC_ALL;
        }
        return RECEIVED_MESSAGES_TOPIC_1;
    }

    public void deleteMessages() {
        RECEIVED_MESSAGES_QUEUE.clear();
        RECEIVED_MESSAGES_TOPIC_1.clear();
        RECEIVED_MESSAGES_TOPIC_ALL.clear();
    }

}

