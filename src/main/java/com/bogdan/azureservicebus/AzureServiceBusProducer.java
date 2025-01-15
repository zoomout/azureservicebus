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

    private final ServiceBusSenderClient senderQueueClient;
    private final ServiceBusSenderClient senderTopicClient;

    public AzureServiceBusProducer(
            @Value("${azure.servicebus.connection-string}") String connectionString,
            @Value("${azure.servicebus.queue-name}") String queueName,
            @Value("${azure.servicebus.topic-name}") String topicName
    ) {
        this.senderQueueClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient();
        this.senderTopicClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .topicName(topicName)
                .buildClient();
    }

    public void sendMessageToQueue(String messageContent) {
        ServiceBusMessage message = new ServiceBusMessage(messageContent);
        senderQueueClient.sendMessage(message);
        LOGGER.info("Message sent to queue: {}", messageContent);
    }

    public void sendMessageToTopic(String messageContent, String correlationId) {
        ServiceBusMessage message = new ServiceBusMessage(messageContent);
        if (correlationId != null) {
            message.setCorrelationId(correlationId);
        }
        senderTopicClient.sendMessage(message);
        LOGGER.info("Message sent to topic: {}", messageContent);
    }

}

