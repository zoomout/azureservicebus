package com.bogdan.azureservicebus;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AzureServiceBusProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureServiceBusProducer.class);

    private final ServiceBusSenderClient senderClient;

    public AzureServiceBusProducer(
            @Value("${azure.servicebus.connection-string}") String connectionString,
            @Value("${azure.servicebus.queue-name}") String queueName
    ) {
        this.senderClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient();
    }

    public void sendMessage(String messageContent) {
        ServiceBusMessage message = new ServiceBusMessage(messageContent);
        senderClient.sendMessage(message);
        LOGGER.info("Message sent: {}", messageContent);
    }

    public void close() {
        senderClient.close();
    }
}

