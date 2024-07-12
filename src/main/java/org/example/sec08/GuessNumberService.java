package org.example.sec08;

import io.grpc.stub.StreamObserver;
import org.example.models.sec08.GuessNumberGrpc;
import org.example.models.sec08.GuessRequest;
import org.example.models.sec08.GuessResponse;
import org.example.models.sec08.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

import static org.example.models.sec08.Result.*;

public class GuessNumberService extends GuessNumberGrpc.GuessNumberImplBase {

    @Override
    public StreamObserver<GuessRequest> makeGuess(StreamObserver<GuessResponse> responseObserver) {
        return new RequestHandler(responseObserver);
    }

    private static class RequestHandler implements StreamObserver<GuessRequest> {

        private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

        private final StreamObserver<GuessResponse> responseObserver;
        private int attempt;
        private final int secretNumber;


        public RequestHandler(StreamObserver<GuessResponse> responseObserver) {
            this.responseObserver = responseObserver;
            this.attempt = 0;
            this.secretNumber = ThreadLocalRandom.current().nextInt(1, 101);
            LOGGER.info("The secret number is : [[[==={}===]]]", secretNumber);
        }

        @Override
        public void onNext(GuessRequest request) {
            LOGGER.info("User guess number : {}", request.getValue());

            attempt++;
            var result = checkAndGetResult(request);

            LOGGER.info("Result guess is : {}", result);

            //send result to client
            responseToClient(result);
        }

        @Override
        public void onError(Throwable t) {
            LOGGER.info("Client have error");
            responseObserver.onCompleted();
        }

        @Override
        public void onCompleted() {
            LOGGER.info("End by client");
            responseObserver.onCompleted();
        }

        private void responseToClient(Result result) {

            responseObserver.onNext(GuessResponse.newBuilder()
                    .setAttempt(attempt)
                    .setResult(result)
                    .build());
        }

        private Result checkAndGetResult(GuessRequest request) {
            Result result;

            if (request.getValue() < secretNumber) {
                result = TOO_LOW;
            } else if (request.getValue() > secretNumber) {
                result = TOO_HIGH;
            } else {
                result = CORRECT;
            }

            return result;
        }
    }
}
