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


    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody String message) {
        azureServiceBusProducer.sendMessage(message);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/receive/{count}")
    public ResponseEntity<List<String>> getMessages(@PathVariable Integer count) {
        return ResponseEntity.ok(azureServiceBusConsumer.receiveMessages(count));
    }

}
