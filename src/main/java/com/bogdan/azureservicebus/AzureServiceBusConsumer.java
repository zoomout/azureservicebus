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

    private final ServiceBusReceiverAsyncClient receiverClient;

    private static final List<String> RECEIVED_MESSAGES = new CopyOnWriteArrayList<>();

    public AzureServiceBusConsumer(
            @Value("${azure.servicebus.connection-string}") String connectionString,
            @Value("${azure.servicebus.queue-name}") String queueName
    ) {
        this.receiverClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .queueName(queueName)
                .buildAsyncClient();
        receiverClient.receiveMessages().onBackpressureBuffer(1000)
                .doOnNext(message -> {
                            RECEIVED_MESSAGES.add(message.getBody().toString());
                            LOGGER.info("Received message: {}", message);
                        }
                )
                .subscribe();
    }

    public List<String> receiveMessages(int count) {
        return RECEIVED_MESSAGES.stream().limit(count).toList();
    }

    public void close() {
        receiverClient.close();
    }
}

