package org.example.sec06;

import io.grpc.ManagedChannelBuilder;
import org.example.common.GrpcServer;
import org.example.models.sec06.BalanceCheckRequest;
import org.example.models.sec06.BankServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcClient.class);

    public static void main(String[] args) {

        //one
        var channel = ManagedChannelBuilder
                .forAddress("localhost", 8000)
                .usePlaintext()
                .build();

        //one
        var stub = BankServiceGrpc.newBlockingStub(channel);

        //many
        var balance = stub.getAccountBalance(
                        BalanceCheckRequest.newBuilder()
                        .setAccountNumber(2)
                        .build());

        LOGGER.info("{}", balance);

    }
}