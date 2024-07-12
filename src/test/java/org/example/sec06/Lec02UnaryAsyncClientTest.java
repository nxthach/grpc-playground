package org.example.sec06;

import com.google.protobuf.Empty;
import org.example.common.ResponseObserver;
import org.example.models.sec06.AccountBalance;
import org.example.models.sec06.BalanceCheckRequest;
import org.example.models.sec06.ListAccountBalance;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class Lec02UnaryAsyncClientTest extends AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lec02UnaryAsyncClientTest.class);

    @Test
    public void getAccountBalance(){
        LOGGER.info("Start test...");

        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        var observer = ResponseObserver.<AccountBalance>create();

        this.bankStub.getAccountBalance(request, observer);

        LOGGER.info("Waiting test finish...");
        observer.await();

        assertEquals(1, observer.getData().size());
        assertEquals(100, observer.getData().get(0).getBalance());
        assertNull(observer.getThrowable());

        LOGGER.info("End test...");

    }

    @Test
    public void getAllAccount(){
        LOGGER.info("Start test...");

        var observer = ResponseObserver.<ListAccountBalance>create();

        //WHEN
        this.bankStub.getAllAccount(Empty.getDefaultInstance(), observer);

        LOGGER.info("Waiting test finish...");
        observer.await();

        //THEN
        assertEquals(1, observer.getData().size());
        assertEquals(10, observer.getData().getFirst().getAccountsCount());
        assertNull(observer.getThrowable());

        LOGGER.info("End test...");

    }
}
