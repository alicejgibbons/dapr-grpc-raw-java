# Dapr gRPC Raw Pub/Sub Example (Java)

This repository contains a minimal end-to-end example of using Dapr's Pub/Sub building block over **gRPC** in **Java**, with **raw payloads** (i.e., not wrapped as CloudEvents).

## Components

- `publisher`: Publishes raw messages using Dapr's Java SDK over gRPC
- `subscriber`: gRPC server that subscribes to the topic and receives raw payloads

## Requirements

- Java 17+
- Maven
- Dapr CLI
- Docker (for Redis)
- Redis running locally (`docker run -d -p 6379:6379 redis`)

## How to Run

### 1. Redis PubSub Component

Create a `components/redis-pubsub.yaml` file:

```yaml
apiVersion: dapr.io/v1alpha1
kind: Component
metadata:
  name: redis-pubsub
spec:
  type: pubsub.redis
  version: v1
  metadata:
    - name: redisHost
      value: "localhost:6379"
    - name: rawPayload
      value: "true"
```

### 2. Build Projects

From each subdirectory:

```bash
mvn clean package
```

### 3. Run Subscriber

```bash
dapr run --app-id subscriber   --app-port 50051   --app-protocol grpc   --resources-path ./components   -- java -jar subscriber/target/subscriber-1.0-SNAPSHOT.jar
```

### 4. Run Publisher

```bash
dapr run --app-id publisher   --resources-path ./components   -- java -jar publisher/target/publisher-1.0-SNAPSHOT.jar
```

### Expected Output

Subscriber will log:

```
📥 Received raw message: {"message":"Hello from gRPC Publisher!"}
```

---

## License

MIT
