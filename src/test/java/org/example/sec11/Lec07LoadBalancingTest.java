package org.example.sec11;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.common.GrpcServer;
import org.example.common.ResponseObserver;
import org.example.models.sec06.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Lec07LoadBalancingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lec07LoadBalancingTest.class);
    protected ManagedChannel channel;
    protected BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;
    private BankServiceGrpc.BankServiceStub bankStub;

    @BeforeAll
    public void setUp() {
        this.channel = ManagedChannelBuilder.forAddress("localhost", 8585)
                .usePlaintext()
                .build();
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
        this.bankStub = BankServiceGrpc.newStub(channel);
    }

    @Test
    public void loadBalancingDemo() {

        for (int i = 1; i <= 10; i++) {
            var request = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(i)
                    .build();

            var response = this.bankBlockingStub.getAccountBalance(request);
            LOGGER.info("{}", response);
        }
    }

    @Test
    public void loadBalancingClientStreamingDemo() {

        var responseObserver = ResponseObserver.<AccountBalance>create();
        var requestObserver = this.bankStub.deposit(responseObserver);

        //set account number on first stream
        requestObserver.onNext(DepositRequest.newBuilder()
                .setAccountNumber(1)
                .build());

        //set for next 10 money
        IntStream.rangeClosed(1, 30)
                .mapToObj(e -> DepositRequest.newBuilder()
                        .setMoney(Money.newBuilder()
                                .setAmount(10)
                                .build())
                        .build())
                .forEach(e -> {
                        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                        requestObserver.onNext(e);
                });

        //done send request
        requestObserver.onCompleted();

        //wait till complete receive all request
        responseObserver.await();
    }

    @AfterAll
    public void tearDown() {
        this.channel.shutdownNow();
    }

}
