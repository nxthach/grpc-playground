package org.example.sec06;

import com.google.common.util.concurrent.Uninterruptibles;
import org.example.common.ResponseObserver;
import org.example.models.sec06.AccountBalance;
import org.example.models.sec06.DepositRequest;
import org.example.models.sec06.Money;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class Lec04ClientStreamTest extends AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lec04ClientStreamTest.class);

    @Test
    public void deposit(){
        LOGGER.info("Start test...");

        var responseObserver = ResponseObserver.<AccountBalance>create();

        var requestObserver = this.bankStub.deposit(responseObserver);

        //set account number on first stream
        requestObserver.onNext(DepositRequest.newBuilder()
                        .setAccountNumber(1)
                .build());

        //set for next 10 money
        IntStream.rangeClosed(1, 10)
                .mapToObj(e -> DepositRequest.newBuilder()
                        .setMoney(Money.newBuilder()
                                .setAmount(10)
                                .build())
                        .build())
                .forEach(requestObserver::onNext);

        //done send request
        requestObserver.onCompleted();

        //wait till complete
        responseObserver.await();

        assertEquals(1, responseObserver.getData().size());
        assertEquals(200, responseObserver.getData().getFirst().getBalance());
        assertNull(responseObserver.getThrowable());

        LOGGER.info("End test...");

    }

    @Test
    public void depositWhenCancel(){
        LOGGER.info("Start test...");

        var responseObserver = ResponseObserver.<AccountBalance>create();

        var requestObserver = this.bankStub.deposit(responseObserver);

        //set account number on first stream
        requestObserver.onNext(DepositRequest.newBuilder()
                .setAccountNumber(1)
                .build());

        //
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

        //done send request
        requestObserver.onError(new RuntimeException());


        assertEquals(0, responseObserver.getData().size());
        assertNotNull(responseObserver.getThrowable());

        LOGGER.info("End test...");

    }




}
