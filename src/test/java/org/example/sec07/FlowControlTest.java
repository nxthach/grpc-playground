package org.example.sec07;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.stub.StreamObserver;
import org.example.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.models.sec07.FlowControlServiceGrpc;
import org.example.models.sec07.Output;
import org.example.models.sec07.RequestSize;
import org.example.sec06.Lec04ClientStreamTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class FlowControlTest extends AbstractChannelTest {

    private final GrpcServer server = GrpcServer.create(new FlowControlService());
    private FlowControlServiceGrpc.FlowControlServiceStub stub;

    @BeforeAll
    public void setup(){
        this.server.start();
        this.stub = FlowControlServiceGrpc.newStub(channel);
    }

    @Test
    public void getMessages(){

        var responseObserver = new ResponseHandler();
        var requestObserver = this.stub.getMessages(responseObserver);
        responseObserver.setRequestObserver(requestObserver);

        responseObserver.request(ThreadLocalRandom.current().nextInt(1, 6));
        responseObserver.await();

    }

    @AfterAll
    public void tearDown(){
        this.server.stop();
    }

    private static class ResponseHandler implements StreamObserver<Output> {

        private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);

        private StreamObserver<RequestSize> requestObserver;
        private int size;

        private CountDownLatch latch;

        public ResponseHandler() {
            this.size = 0;
            this.latch = new CountDownLatch(1);
        }

        @Override
        public void onNext(Output value) {
            this.size--;

            //process message have received
            processData(value);

            //request more once no more message to process
            if (size == 0) {
                LOGGER.info("----------------------------");
                request(ThreadLocalRandom.current().nextInt(1, 6));
            }

        }

        @Override
        public void onError(Throwable t) {
            //TODO
        }

        @Override
        public void onCompleted() {
            latch.countDown();

            LOGGER.info("Server emit data completed");
            requestObserver.onCompleted();

        }

        public void request(int size) {
            LOGGER.info("Request size of data : {}", size);
            this.size = size;
            requestObserver.onNext(RequestSize.newBuilder().setSize(size).build());
        }


        private void processData(Output value) {
            LOGGER.info("Processing value : {}", value);

            //simulate slow process on client
            Uninterruptibles.sleepUninterruptibly(
                    ThreadLocalRandom.current().nextInt(50, 200),
                    TimeUnit.MILLISECONDS
            );
        }

        public void setRequestObserver(StreamObserver<RequestSize> requestObserver) {
            this.requestObserver = requestObserver;
        }

        public void await() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
