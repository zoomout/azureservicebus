# azureservicebus

## Run tests
```bash
mvn clean test
```

## Build
```bash
mvn clean package -DskipTests=true
```

## Start docker app
```bash
docker compose -f ./compose-test-app.yml up --build -d
```

## Stop docker app
```bash
docker compose -f ./compose-test-app.yml down -v
```

## Check docker container health check log
```bash
docker inspect --format='{{json .State.Health}}' <container_name> | jq
```

## Example log
```
00:01:57.362 [http-nio-8080-exec-8] INFO  c.a.m.s.ServiceBusSenderAsyncClient - {"az.sdk.message":"Sending batch.","batchSize":1}
00:01:57.374 [http-nio-8080-exec-8] INFO  c.b.a.AzureServiceBusProducer - Message sent to topic: DONE!!!!
00:01:57.387 [receiverPump-11] INFO  c.b.a.AzureServiceBusConsumer - Received message from topic sub All: DONE!!!!
```