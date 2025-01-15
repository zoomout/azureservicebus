package com.bogdan.azureservicebus;

import io.restassured.RestAssured;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.ComposeContainer;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ServiceBusTest {


    public static ComposeContainer TEST_COMPOSE = new ComposeContainer(new File("src/test/resources/compose-test.yml"))
            .withExposedService("emulator", 5672);

    @BeforeAll
    static void setupCompose() {
        RestAssured.baseURI = "http://localhost:8080";
        TEST_COMPOSE.start();
    }

    @BeforeEach
    void setupServiceBus() {
        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            RestAssured
                    .when().get("/actuator/health")
                    .then().statusCode(200);
        });
        RestAssured
                .when().delete("/messages")
                .then().statusCode(200);
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
    void test0ServiceBusError() {
        String message = "ErrorMessage";

        RestAssured.given().body(message)
                .when().post("/messages/error")
                .then().statusCode(202);

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            String bodyAll = RestAssured
                    .when().get("/messages/dead")
                    .then().statusCode(200)
                    .and()
                    .extract().body().asString();
            assertThat(bodyAll).isEqualTo("[\"" + message + "\"]");
        });

    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
    void test1ServiceBusQueue() {
        String message1 = "Test message 1";
        String message2 = "Test message 2";

        RestAssured.given().body(message1)
                .when().post("/messages")
                .then().statusCode(202);

        RestAssured.given().body(message2)
                .when().post("/messages")
                .then().statusCode(202);

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            String body = RestAssured
                    .when().get("/messages")
                    .then().statusCode(200)
                    .and()
                    .extract().body().asString();
            assertThat(body).isEqualTo("[\"" + message1 + "\",\"" + message2 + "\"]");
        });

    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
    void test2ServiceBusTopic() {
        String message1 = "Test message topic 1";
        String message2 = "Test message topic 2";

        RestAssured.given().body(message1)
                .when().post("/messages/correlationId1")
                .then().statusCode(202);

        RestAssured.given().body(message2)
                .when().post("/messages/correlationIdAny")
                .then().statusCode(202);

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            String bodyAll = RestAssured
                    .when().get("/messages/all")
                    .then().statusCode(200)
                    .and()
                    .extract().body().asString();
            assertThat(bodyAll).isEqualTo("[\"" + message1 + "\",\"" + message2 + "\"]");

            String bodyCorrelationId1 = RestAssured
                    .when().get("/messages/correlationId1")
                    .then().statusCode(200)
                    .and()
                    .extract().body().asString();
            assertThat(bodyCorrelationId1).isEqualTo("[\"" + message1 + "\"]");
        });

    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
    void test3ServiceBusErrorRetryDeferred() {
        String message = "ErrorMessageDeferred";

        Instant start = Instant.now();
        RestAssured.given().body(message)
                .when().post("/messages/errorDeferred")
                .then().statusCode(202);

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            String bodyAll = RestAssured
                    .when().get("/messages/all")
                    .then().statusCode(200)
                    .and()
                    .extract().body().asString();

            assertThat(bodyAll).isEqualTo("[\"" + message + "+2Sec" + "\"]");
        });
        Instant end = Instant.now();
        assertThat(end).isAfter(start.plusSeconds(2));

    }

    @AfterAll
    static void tearDownServiceBus() {
        TEST_COMPOSE.stop();
    }

}
