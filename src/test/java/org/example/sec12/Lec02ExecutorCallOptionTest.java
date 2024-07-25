package org.example.sec12;

import org.example.common.ResponseObserver;
import org.example.models.sec12.BalanceCheckRequest;
import org.example.models.sec12.Money;
import org.example.models.sec12.WithdrawRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Lec02ExecutorCallOptionTest extends AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lec02ExecutorCallOptionTest.class);

    @Test
    public void executorDemo(){
        var request = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(30)
                .build();

        var response = ResponseObserver.<Money>create();

        this.bankStub.withExecutor(Executors.newVirtualThreadPerTaskExecutor())
                        .withdraw(request, response);

        response.await();

    }
}
