package org.example.sec06;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.models.sec06.AccountBalance;
import org.example.models.sec06.BalanceCheckRequest;
import org.example.models.sec06.BankServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class GrpcAsyncClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcAsyncClient.class);

    public static void main(String[] args) throws InterruptedException {

        //one
        var channel = ManagedChannelBuilder
                .forAddress("localhost", 8000)
                .usePlaintext()
                .build();

        //one
        var stub = BankServiceGrpc.newStub(channel);

        //many
        stub.getAccountBalance(
                BalanceCheckRequest.newBuilder()
                        .setAccountNumber(2)
                        .build(),
                new StreamObserver<AccountBalance>() {
                    @Override
                    public void onNext(AccountBalance accountBalance) {
                        LOGGER.info("Received AccountBalance : {}", accountBalance);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onCompleted() {
                        LOGGER.info("Completed!");
                    }
                }
        );

        LOGGER.info("DONE!!!");
        Thread.sleep(Duration.ofSeconds(1));

    }
}