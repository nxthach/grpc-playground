package org.example.sec12;

import org.example.models.sec12.BalanceCheckRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec01GzipCallOptionTest extends AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lec01GzipCallOptionTest.class);

    @Test
    public void gzipDemo(){
        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        var response = this.bankBlockingStub
                .withCompression("gzip")
                .getAccountBalance(request);

        LOGGER.info("{}", response);

    }
}
