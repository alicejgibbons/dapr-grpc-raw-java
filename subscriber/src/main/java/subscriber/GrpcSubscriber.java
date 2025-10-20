package subscriber;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Empty;

import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.DaprAppCallbackProtos;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class GrpcSubscriber extends AppCallbackGrpc.AppCallbackImplBase {
    private final List<DaprAppCallbackProtos.TopicSubscription> topicSubscriptionList = new ArrayList<>();


  @Override
  public void onTopicEvent(DaprAppCallbackProtos.TopicEventRequest request, StreamObserver<DaprAppCallbackProtos.TopicEventResponse> responseObserver) {
    String messageToPrint = null;
    if (request.hasExtensions()) {
      if (request.getExtensions().getFieldsMap().containsKey("message")) {
        messageToPrint = request.getExtensions().getFieldsMap().get("message").getStringValue();
      }
    }
    if (messageToPrint == null || messageToPrint.isEmpty()) {
      messageToPrint = request.getData().toStringUtf8();
    }
    System.out.println(messageToPrint);

    DaprAppCallbackProtos.TopicEventResponse response = DaprAppCallbackProtos.TopicEventResponse.newBuilder()
        .setStatus(DaprAppCallbackProtos.TopicEventResponse.TopicEventResponseStatus.SUCCESS)
        .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void listTopicSubscriptions(Empty request, StreamObserver<DaprAppCallbackProtos.ListTopicSubscriptionsResponse> responseObserver) {
    registerConsumer("redis-pubsub", "mytopic", false);
    try {
      DaprAppCallbackProtos.ListTopicSubscriptionsResponse.Builder builder = DaprAppCallbackProtos
          .ListTopicSubscriptionsResponse.newBuilder();
      topicSubscriptionList.forEach(builder::addSubscriptions);
      DaprAppCallbackProtos.ListTopicSubscriptionsResponse response = builder.build();
      responseObserver.onNext(response);
    } catch (Throwable e) {
      responseObserver.onError(e);
    } finally {
      responseObserver.onCompleted();
    }
  }

  public void registerConsumer(String pubsubName, String topic, boolean isBulkMessage) {
    topicSubscriptionList.add(DaprAppCallbackProtos.TopicSubscription
        .newBuilder()
        .setPubsubName(pubsubName)
        .setTopic(topic)//.putMetadata("rawPayload", "true")
        .setBulkSubscribe(DaprAppCallbackProtos.BulkSubscribeConfig.newBuilder().setEnabled(isBulkMessage))
        .build());
  }

  public static void main(String[] args) throws Exception {
    Server server = ServerBuilder.forPort(50051)
        .addService(new GrpcSubscriber())
        .build()
        .start();

    System.out.println("✅ gRPC Subscriber is running on port 50051");
    server.awaitTermination();
  }
  
}