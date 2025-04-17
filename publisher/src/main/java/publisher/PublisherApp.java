package publisher;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;

public class PublisherApp {
  public static void main(String[] args) {
    try (DaprClient client = new DaprClientBuilder().build()) {
      byte[] rawData = "{\"message\":\"Hello from gRPC Publisher!\"}".getBytes(StandardCharsets.UTF_8);

      Map<String, String> metadata = new HashMap<>();
      metadata.put("rawPayload", "true");

      client.publishEvent("redis-pubsub", "mytopic", rawData, metadata).block();
      System.out.println("✅ Published raw message.");
    } catch (Exception e) {
      System.err.println("❌ Failed to publish event: " + e.getMessage());
      e.printStackTrace();
    }
  }
}