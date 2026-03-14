# Azure Service Bus Demo

A Spring Boot 3.4.2 demonstration project showcasing integration with Azure Service Bus using the Spring Cloud Azure Starter. This project includes support for the Azure Service Bus Emulator for local development.

## Features

- **Queue Support:** Send and receive messages from Azure Service Bus queues.
- **Topic/Subscription Support:** Send messages to topics and receive from specific subscriptions.
- **REST API:** Simple HTTP endpoints to interact with the messaging system.
- **Local Development:** Integrated with Azure Service Bus Emulator via Docker Compose.
- **Containerized:** Includes Dockerfile and Kubernetes manifests for deployment.

## Prerequisites

- **Java:** Version 21
- **Build Tool:** Maven 3.x
- **Containerization:** Docker and Docker Compose (for emulator and app)
- **Utilities:** `curl` and `jq` (optional, for testing endpoints)

## Getting Started

### 1. Start the Infrastructure (Azure Service Bus Emulator)

This project uses the Azure Service Bus Emulator for local development. Start the emulator and its required SQL Server database:

```bash
docker compose -f ./compose-test.yml up -d
```

### 2. Run the Application Locally

Once the emulator is running, you can start the Spring Boot application:

```bash
mvn spring-boot:run
```

Alternatively, you can run both the emulator and the app using:

```bash
docker compose -f ./compose-test-app.yml up --build -d
```

## API Reference

The application exposes the following REST endpoints:

### Queue Operations

**Send Message to Queue**
```bash
curl -X POST -d "Hello Queue" http://localhost:8080/messages
```

**Receive Messages from Queue**
```bash
curl http://localhost:8080/messages
```

### Topic Operations

**Send Message to Topic**
```bash
# correlationId determines which subscription receives the message
# Use 'correlationId1' for subscription.1
# Use 'deadLetterQueue1' for subscription.dead
curl -X POST -d "Hello Topic" http://localhost:8080/messages/correlationId1
```

**Receive Messages from Subscriptions**
- `all`: All messages (except those with correlationId `deadLetterQueue1`).
- `dead`: Messages with correlationId `deadLetterQueue1`.
- `1`: Messages with correlationId `correlationId1`.

```bash
# Receive from 'subscription.all'
curl http://localhost:8080/messages/all

# Receive from 'subscription.dead'
curl http://localhost:8080/messages/dead

# Receive from 'subscription.1'
curl http://localhost:8080/messages/1
```

### Special Behaviors

- **Dead Lettering Simulation:** Sending `ErrorMessage` to the topic will cause the consumer to manually forward it to the dead letter subscription.
- **Deference Simulation:** Sending `ErrorMessageDeferred` to the topic will trigger a deferred message send.

### Clean Up
```bash
# Clears all locally stored messages in the application
curl -X DELETE http://localhost:8080/messages
```

## Configuration

Core configuration is located in `src/main/resources/application.properties`. By default, it's set to use the local emulator:

```properties
azure.servicebus.connection-string=Endpoint=sb://hello:5672;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=SAS_KEY_VALUE;UseDevelopmentEmulator=true;
azure.servicebus.queue-name=test-queue-1
azure.servicebus.topic-name=topic.1
```

## Testing

Run unit and integration tests (uses Testcontainers):

```bash
mvn clean test
```

## Maintenance Commands

### Build the Application
```bash
mvn clean package -DskipTests=true
```

### Stop Infrastructure
```bash
docker compose -f ./compose-test.yml down -v
```

### Check Health Logs
```bash
docker inspect --format='{{json .State.Health}}' <container_name> | jq
```

## Deployment

The project includes:
- **Dockerfile:** For building the application image.
- **K8s Manifests:** Located in the `k8s/` directory for deploying to Kubernetes (Namespace, Service, Deployment, ConfigMap).
- **GitHub Workflows:** `.github/workflows/build.yml` for CI/CD.
