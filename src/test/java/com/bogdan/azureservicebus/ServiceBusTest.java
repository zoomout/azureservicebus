package com.bogdan.azureservicebus;

import io.restassured.RestAssured;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.ComposeContainer;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ServiceBusTest {


    public static ComposeContainer TEST_COMPOSE = new ComposeContainer(new File("src/test/resources/compose-test.yml"))
            .withExposedService("emulator", 5672);

    @BeforeEach
    void setupServiceBus() {
        TEST_COMPOSE.start();
        RestAssured.baseURI = "http://localhost:8080";
        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            RestAssured
                    .when().get("/actuator/health")
                    .then().statusCode(200);
        });
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
    void testServiceBus() {
        String message1 = "Test message 1";
        String message2 = "Test message 2";

        RestAssured.given().body(message1)
                .when().post("/send")
                .then().statusCode(202);

        RestAssured.given().body(message2)
                .when().post("/send")
                .then().statusCode(202);

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            String body = RestAssured
                    .when().get("/receive/2")
                    .then().statusCode(200)
                    .and()
                    .extract().body().asString();
            assertThat(body).isEqualTo("[\"" + message1 + "\",\"" + message2 + "\"]");
        });

    }

    @AfterAll
    static void tearDownServiceBus() {
        TEST_COMPOSE.stop();
    }

}
