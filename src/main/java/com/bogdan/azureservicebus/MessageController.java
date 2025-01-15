package com.bogdan.azureservicebus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageController {

    private final AzureServiceBusProducer azureServiceBusProducer;
    private final AzureServiceBusConsumer azureServiceBusConsumer;

    public MessageController(
            AzureServiceBusProducer azureServiceBusProducer,
            AzureServiceBusConsumer azureServiceBusConsumer
    ) {
        this.azureServiceBusProducer = azureServiceBusProducer;
        this.azureServiceBusConsumer = azureServiceBusConsumer;
    }

    @PostMapping("/messages")
    public ResponseEntity<Void> sendMessage(@RequestBody String message) {
        azureServiceBusProducer.sendMessageToQueue(message);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/messages/{correlationId}")
    public ResponseEntity<Void> sendMessage(@RequestBody String message, @PathVariable String correlationId) {
        azureServiceBusProducer.sendMessageToTopic(message, correlationId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/messages")
    public ResponseEntity<List<String>> getMessages() {
        return ResponseEntity.ok(azureServiceBusConsumer.receiveMessagesQueue());
    }

    @GetMapping("/messages/{subId}")
    public ResponseEntity<List<String>> getMessages(@PathVariable String subId) {
        return ResponseEntity.ok(azureServiceBusConsumer.receiveMessagesTopic(subId));
    }

    @DeleteMapping("/messages")
    public ResponseEntity<Void> deleteMessages() {
        azureServiceBusConsumer.deleteMessages();
        return ResponseEntity.ok().build();
    }

}
