package org.example.sec06;

import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.models.sec06.AccountBalance;
import org.example.models.sec06.BalanceCheckRequest;
import org.example.models.sec06.BankServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class GrpcFutureClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcFutureClient.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        //one
        var channel = ManagedChannelBuilder
                .forAddress("localhost", 8000)
                .usePlaintext()
                .build();

        //one
        var stub = BankServiceGrpc.newFutureStub(channel);

        //many
        var accountBalanceFuture = stub.getAccountBalance(
                BalanceCheckRequest.newBuilder()
                        .setAccountNumber(2)
                        .build());

        var accountBalance = accountBalanceFuture.get();

        LOGGER.info("{}", accountBalance);

    }
}