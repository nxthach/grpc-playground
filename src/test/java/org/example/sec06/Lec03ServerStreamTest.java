package org.example.sec06;

import org.example.common.ResponseObserver;
import org.example.models.sec06.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class Lec03ServerStreamTest extends AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lec03ServerStreamTest.class);

    @Test
    public void blockingClientWithdraw(){
        LOGGER.info("Start test...");

        var request = WithdrawRequest.newBuilder()
                .setAccountNumber(2)
                .setAmount(20)
                .build();

        var results = this.bankBlockingStub.withdraw(request);

        int count = 0;
        while (results.hasNext()){
            LOGGER.info("Received : {}", results.next());
            count++;
        }

        assertEquals(2, count);

        LOGGER.info("End test...");

    }

    @Test
    public void asyncClientWithdraw(){
        LOGGER.info("Start test...");

        var request = WithdrawRequest.newBuilder()
                .setAccountNumber(2)
                .setAmount(20)
                .build();

        var observer = ResponseObserver.<Money>create();

        //WHEN
        this.bankStub.withdraw(request, observer);
        observer.await(); //wait till receive done

        //THEN
        assertEquals(2, observer.getData().size());
        assertEquals(10, observer.getData().getFirst().getAmount());
        assertNull(observer.getThrowable());

        LOGGER.info("End test...");

    }


}
