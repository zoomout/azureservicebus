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