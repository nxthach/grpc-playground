package org.example.sec08;

import io.grpc.stub.StreamObserver;
import org.example.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.models.sec08.GuessNumberGrpc;
import org.example.models.sec08.GuessRequest;
import org.example.models.sec08.GuessResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class GuessNumberTest extends AbstractChannelTest {

    private final GrpcServer server = GrpcServer.create(new GuessNumberService());
    private GuessNumberGrpc.GuessNumberStub stub;

    @BeforeAll
    public void setUp(){
        this.server.start();
        this.stub = GuessNumberGrpc.newStub(channel);
    }

    @Test
    public void makeGuess(){

        var responseObserver = new ResponseHandler();
        var requestObserver = this.stub.makeGuess(responseObserver);

        responseObserver.setRequestObserver(requestObserver);

        //start request to server
        requestObserver.onNext(GuessRequest.newBuilder().setValue(50).build());

        //wait till server done
        responseObserver.await();
    }

    @AfterAll
    public void tearDown(){
        this.server.stop();
    }

    private static class ResponseHandler implements StreamObserver<GuessResponse> {

        private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);

        private final CountDownLatch latch = new CountDownLatch(1);

        private StreamObserver<GuessRequest> requestObserver;
        private int lower;
        private int higher;
        private int guessValue;

        public ResponseHandler() {
            this.lower = 0;
            this.higher = 100;
            this.guessValue = determineGuessValue();
        }

        @Override
        public void onNext(GuessResponse response) {

            LOGGER.info("Client start handle response");
            switch (response.getResult()){
                case CORRECT -> requestObserver.onCompleted();
                case TOO_LOW -> continueGuessWhenLower();
                case TOO_HIGH -> continueGuessWhenHigher();
            }
            LOGGER.info("Client end handle response");
            LOGGER.info("--------------------------");

        }

        @Override
        public void onError(Throwable t) {
            LOGGER.info("Error : {}", t.getMessage());
            latch.countDown();
        }

        @Override
        public void onCompleted() {
            LOGGER.info("Completed");
            latch.countDown();
        }

        public void setRequestObserver(StreamObserver<GuessRequest> requestObserver) {
            this.requestObserver = requestObserver;
        }

        public void await(){
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void continueGuessWhenLower(){
            this.lower = this.guessValue;
            doGuess();
        }

        private void continueGuessWhenHigher(){
            this.higher = this.guessValue;
            doGuess();
        }

        private void doGuess() {

            this.guessValue = determineGuessValue();
            LOGGER.info("Next value for guess : {}", guessValue);
            requestObserver.onNext(GuessRequest.newBuilder()
                    .setValue(guessValue)
                    .build());
        }

        private int determineGuessValue(){
            return this.lower + (this.higher - this.lower)/2;
        }

    }
}
