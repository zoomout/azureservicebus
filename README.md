# azureservicebus

## Run tests
```
 mvn clean test
```

## Build
```
mvn clean package -DskipTests=true
```

## Start docker app
```
 docker compose -f ./compose-test-app.yml up --build -d
```

## Stop docker app
```
 docker compose -f ./compose-test-app.yml down -v
```

## Check docker container health check log
```
docker inspect --format='{{json .State.Health}}' <container_name> | jq
```