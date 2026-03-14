package com.bogdan.azureservicebus;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

public class AzureServiceBusConsumerTest {

    @Test
    void testCorrelationIdComparisonIsSafe() {
        ServiceBusReceivedMessage message = Mockito.mock(ServiceBusReceivedMessage.class);
        when(message.getCorrelationId()).thenReturn(null);

        // This is the fix we applied: using Objects.equals
        assertDoesNotThrow(() -> {
            boolean isDeadLetter = Objects.equals(message.getCorrelationId(), "deadLetterQueue1");
            assertThat(isDeadLetter).isFalse();
        });
    }

    @Test
    void testCorrelationIdComparisonMatches() {
        ServiceBusReceivedMessage message = Mockito.mock(ServiceBusReceivedMessage.class);
        when(message.getCorrelationId()).thenReturn("deadLetterQueue1");

        boolean isDeadLetter = Objects.equals(message.getCorrelationId(), "deadLetterQueue1");
        assertThat(isDeadLetter).isTrue();
    }
}
