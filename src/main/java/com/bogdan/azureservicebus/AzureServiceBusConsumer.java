package com.bogdan.azureservicebus;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AzureServiceBusConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureServiceBusConsumer.class);

    private final ServiceBusReceiverClient receiverClient;

    public AzureServiceBusConsumer(
            @Value("${azure.servicebus.connection-string}") String connectionString,
            @Value("${azure.servicebus.queue-name}") String queueName
    ) {
        this.receiverClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .queueName(queueName)
                .buildClient();
    }

    public List<String> receiveMessages(int count) {
        return receiverClient.receiveMessages(count, Duration.ofSeconds(1)).stream()
                .peek(message -> {
                    LOGGER.info("Received message: {}", message);
                    receiverClient.complete(message);
                })
                .map(m -> m.getBody().toString())
                .collect(Collectors.toList());
    }

    public void close() {
        receiverClient.close();
    }
}

