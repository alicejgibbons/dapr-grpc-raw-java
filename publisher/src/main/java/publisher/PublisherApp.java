package publisher;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;

public class PublisherApp {
  private static final String DAPR_API_TOKEN = System.getenv().getOrDefault("DAPR_API_TOKEN", "");

  public static void main(String[] args) {
    try (DaprClient client = new DaprClientBuilder().build()) {
      System.out.println("DAPR_API_TOKEN is set to: " + DAPR_API_TOKEN);
      byte[] rawData = "{\"message\":\"Hello from gRPC Publisher!\"}".getBytes(StandardCharsets.UTF_8);

      Map<String, String> metadata = new HashMap<>();
      metadata.put("rawPayload", "true");

      System.out.println("✅ Publishing raw message.");
      client.publishEvent("redis-pubsub", "mytopic", rawData, metadata).block();
      System.out.println("✅ Published raw message.");
    } catch (Exception e) {
      System.err.println("❌ Failed to publish event: " + e.getMessage());
      e.printStackTrace();
    }
  }
}