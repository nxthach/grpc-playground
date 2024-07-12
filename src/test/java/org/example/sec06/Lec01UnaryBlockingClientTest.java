package org.example.sec06;

import com.google.protobuf.Empty;
import org.example.models.sec06.BalanceCheckRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Lec01UnaryBlockingClientTest extends AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lec01UnaryBlockingClientTest.class);

    @Test
    public void getAccountBalance(){
        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        var balance = this.bankBlockingStub.getAccountBalance(request);

        LOGGER.info("Received : {}", balance);

        assertEquals(100, balance.getBalance());

    }

    @Test
    public void getAllAccountBalance(){
        var allAccounts = this.bankBlockingStub.getAllAccount(Empty.getDefaultInstance());

        LOGGER.info("Received : {}", allAccounts);
        assertEquals(10, allAccounts.getAccountsList().size());

    }


}
