package org.example.sec07;

import io.grpc.stub.StreamObserver;
import org.example.models.sec07.FlowControlServiceGrpc;
import org.example.models.sec07.Output;
import org.example.models.sec07.RequestSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class FlowControlService extends FlowControlServiceGrpc.FlowControlServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowControlService.class);

    @Override
    public StreamObserver<RequestSize> getMessages(StreamObserver<Output> responseObserver) {
        return new RequestHandler(responseObserver);
    }

    private static class RequestHandler implements StreamObserver<RequestSize> {
        private final StreamObserver<Output> responseObserver;
        private int emitted;

        public RequestHandler(StreamObserver<Output> responseObserver) {
            this.responseObserver = responseObserver;
            emitted = 0;
        }

        @Override
        public void onNext(RequestSize request) {

            IntStream.rangeClosed(emitted + 1, 100)
                    .limit(request.getSize())
                    .forEach(e -> {
                        LOGGER.info("Emitting value : {}", e);
                        responseObserver.onNext(Output.newBuilder().setValue(e).build());
                    });

            emitted = emitted + request.getSize();

            if (emitted >= 100) {
                LOGGER.info("Server have no data to response");
                responseObserver.onCompleted();
            }
        }

        @Override
        public void onError(Throwable t) {

        }

        @Override
        public void onCompleted() {
            LOGGER.info("Client stop request data.");
            responseObserver.onCompleted();
        }
    }
}
